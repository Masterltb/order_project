# Testing Standards

> **AI Agent Instruction**: Follow these testing conventions for Restaurant Ordering System.

---

## 1. Test Pyramid

```
         /\
        /  \  E2E Tests (5%)
       /----\  - Full order flow
      /      \ 
     /--------\  Integration Tests (25%)
    /          \ - Repository, WebSocket
   /------------\
  /              \  Unit Tests (70%)
 /________________\ - Service, Validation
```

### Coverage Requirements
| Layer | Minimum Coverage |
|-------|-----------------|
| Service | 80% |
| Controller | 70% |
| Repository | 60% |

---

## 2. Test File Naming

```
src/test/java/com/restaurant/backend/
├── ordering/
│   ├── OrderServiceTest.java           # Unit test
│   ├── OrderControllerTest.java        # Controller test
│   ├── OrderRepositoryTest.java        # Integration test
│   └── OrderFlowIntegrationTest.java   # E2E test
├── payment/
│   ├── PaymentServiceTest.java
│   └── PaymentCallbackTest.java
└── realtime/
    └── WebSocketEventTest.java
```

---

## 3. Unit Test Template

### Service Test
```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private DiningTableRepository tableRepository;
    
    @Mock
    private WebSocketEventPublisher eventPublisher;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    @Nested
    @DisplayName("openOrder")
    class OpenOrder {
        
        @Test
        @DisplayName("should return existing open order if exists")
        void shouldReturnExistingOrder() {
            // Given
            Long tableId = 1L;
            Order existingOrder = createOrder(1L, tableId, OrderStatus.PENDING_CONFIRM);
            when(orderRepository.findByTableIdAndIsClosedFalse(tableId))
                .thenReturn(Optional.of(existingOrder));
            
            // When
            OrderResponse result = orderService.openOrder(tableId, 1L);
            
            // Then
            assertThat(result.getId()).isEqualTo(1L);
            verify(orderRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("should create new order if no open order exists")
        void shouldCreateNewOrder() {
            // Given
            Long tableId = 1L;
            DiningTable table = createTable(tableId, "A1");
            when(orderRepository.findByTableIdAndIsClosedFalse(tableId))
                .thenReturn(Optional.empty());
            when(tableRepository.findById(tableId))
                .thenReturn(Optional.of(table));
            when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> {
                    Order o = inv.getArgument(0);
                    o.setId(1L);
                    return o;
                });
            
            // When
            OrderResponse result = orderService.openOrder(tableId, 1L);
            
            // Then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING_CONFIRM);
            verify(eventPublisher).publish(any(OrderEvent.class));
        }
        
        @Test
        @DisplayName("should throw ResourceNotFoundException if table not found")
        void shouldThrowIfTableNotFound() {
            // Given
            Long tableId = 999L;
            when(orderRepository.findByTableIdAndIsClosedFalse(tableId))
                .thenReturn(Optional.empty());
            when(tableRepository.findById(tableId))
                .thenReturn(Optional.empty());
            
            // When/Then
            assertThatThrownBy(() -> orderService.openOrder(tableId, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("DiningTable");
        }
    }
    
    @Nested
    @DisplayName("addItem")
    class AddItem {
        
        @Test
        @DisplayName("should add item to open order")
        void shouldAddItem() { ... }
        
        @Test
        @DisplayName("should throw BusinessException if order is closed")
        void shouldThrowIfOrderClosed() {
            // Given
            Order closedOrder = createOrder(1L, 1L, OrderStatus.SERVED);
            closedOrder.setIsClosed(true);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(closedOrder));
            
            AddItemRequest request = new AddItemRequest(10L, 2, "note", List.of());
            
            // When/Then
            assertThatThrownBy(() -> orderService.addItem(1L, request, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("closed");
        }
    }
    
    // Helper methods
    private Order createOrder(Long id, Long tableId, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        DiningTable table = new DiningTable();
        table.setId(tableId);
        order.setTable(table);
        return order;
    }
    
    private DiningTable createTable(Long id, String code) {
        DiningTable table = new DiningTable();
        table.setId(id);
        table.setTableCode(code);
        return table;
    }
}
```

---

## 4. Controller Test Template

```java
@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Nested
    @DisplayName("POST /orders/open")
    class OpenOrder {
        
        @Test
        @WithMockUser(roles = "STAFF")
        @DisplayName("should return 201 when order created")
        void shouldCreateOrder() throws Exception {
            // Given
            OpenOrderRequest request = new OpenOrderRequest(1L);
            OrderResponse response = new OrderResponse(1L, 1L, "A1", OrderStatus.PENDING_CONFIRM);
            when(orderService.openOrder(eq(1L), anyLong())).thenReturn(response);
            
            // When/Then
            mockMvc.perform(post("/orders/open")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
        }
        
        @Test
        @WithAnonymousUser
        @DisplayName("should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/orders/open")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isUnauthorized());
        }
    }
    
    @Nested
    @DisplayName("POST /orders/{id}/confirm")
    class ConfirmOrder {
        
        @Test
        @WithMockUser(roles = "CASHIER")
        @DisplayName("should confirm order when cashier")
        void shouldConfirmOrder() throws Exception { ... }
        
        @Test
        @WithMockUser(roles = "STAFF")
        @DisplayName("should return 403 when staff tries to confirm")
        void shouldReturn403WhenStaff() throws Exception {
            mockMvc.perform(post("/orders/1/confirm"))
                .andExpect(status().isForbidden());
        }
    }
}
```

---

## 5. Integration Test Template

### Repository Test
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class OrderRepositoryTest {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private DiningTableRepository tableRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("findByTableIdAndIsClosedFalse should return open order")
    void shouldFindOpenOrder() {
        // Given
        DiningTable table = createAndSaveTable("A1");
        Order openOrder = createAndSaveOrder(table, false);
        Order closedOrder = createAndSaveOrder(table, true);
        entityManager.flush();
        
        // When
        Optional<Order> result = orderRepository.findByTableIdAndIsClosedFalse(table.getId());
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(openOrder.getId());
    }
}
```

---

## 6. Key Test Scenarios

### Ordering Flow
| Scenario | Expected |
|----------|----------|
| Staff opens table with no order | New order created, status PENDING_CONFIRM |
| Staff opens table with existing order | Existing order returned |
| Staff adds item to order | Item added, total updated, event published |
| Cashier confirms order | Status → COOKING, confirmedBy set |
| Cashier updates status | Status changed, event published |
| Add item to closed order | BusinessException thrown |

### Concurrency (Optimistic Locking)
| Scenario | Expected |
|----------|----------|
| Two staff add items concurrently | One succeeds, one gets 409 Conflict |
| Update with stale version | ConflictException thrown |

### Payment
| Scenario | Expected |
|----------|----------|
| Cash payment covers full amount | paidTotal updated, order closed |
| Cash payment partial | paidTotal updated, order stays open |
| E-wallet callback valid signature | Payment SUCCEEDED, paidTotal updated |
| E-wallet callback invalid signature | Payment FAILED, log warning |

### Transfers
| Scenario | Expected |
|----------|----------|
| Move items to table without order | New order created, items moved |
| Move items to table with order | Items added to existing order |
| Transfer logged | TableTransfer + TableTransferItem created |

### Role-Based Access
| Scenario | Expected |
|----------|----------|
| STAFF calls confirm endpoint | 403 Forbidden |
| STAFF calls payment endpoint | 403 Forbidden |
| CASHIER calls admin endpoint | 403 Forbidden |
| ADMIN calls any endpoint | 200 OK |

---

## 7. WebSocket Test Template

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class WebSocketEventTest {
    
    @Value("${local.server.port}")
    private int port;
    
    private WebSocketStompClient stompClient;
    private BlockingQueue<OrderEvent> receivedEvents;
    
    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        receivedEvents = new LinkedBlockingQueue<>();
    }
    
    @Test
    @DisplayName("POS should receive ORDER_ITEM_ADDED event")
    void shouldReceiveOrderItemAddedEvent() throws Exception {
        // Given
        String url = "ws://localhost:" + port + "/ws";
        StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {})
            .get(5, TimeUnit.SECONDS);
        
        session.subscribe("/topic/pos/orders", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return OrderEvent.class;
            }
            
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedEvents.add((OrderEvent) payload);
            }
        });
        
        // When - add item via REST API
        // POST /orders/1/items
        
        // Then
        OrderEvent event = receivedEvents.poll(5, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.getType()).isEqualTo("ORDER_ITEM_ADDED");
    }
}
```

---

## 8. Test Data Builders

```java
public class TestDataBuilder {
    
    public static DiningTable.Builder table() {
        return DiningTable.builder()
            .tableCode("A1")
            .capacity(4)
            .status(TableStatus.AVAILABLE);
    }
    
    public static Order.Builder order() {
        return Order.builder()
            .status(OrderStatus.PENDING_CONFIRM)
            .isClosed(false)
            .subTotal(BigDecimal.ZERO)
            .grandTotal(BigDecimal.ZERO)
            .paidTotal(BigDecimal.ZERO);
    }
    
    public static MenuItem.Builder menuItem() {
        return MenuItem.builder()
            .name("Cà phê sữa đá")
            .basePrice(new BigDecimal("25000"))
            .status(MenuItemStatus.AVAILABLE);
    }
}
```

---

## 9. Test Configuration

### application-test.properties
```properties
# H2 in-memory for tests
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# Disable Flyway for tests
spring.flyway.enabled=false

# JWT secret for tests
jwt.secret=test-secret-key-for-testing-only
jwt.expiration=3600000
```

---

## 10. Test Checklist

Before submitting:

- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Coverage meets minimum requirements
- [ ] Happy path tested
- [ ] Error cases tested
- [ ] Role-based access tested
- [ ] Concurrency (optimistic locking) tested
- [ ] WebSocket events tested
- [ ] No hardcoded test data that conflicts

package service;

import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MiscServiceTests {
    private static MiscService service;

    @BeforeAll
    public static void init() {
        service = new MiscService(
                new MemoryAuthDAO(),
                new MemoryGameDAO(),
                new MemoryUserDAO());
    }

    @Test
    @Order(0)
    public void testClear() throws ServiceException {
        service.clearDatabase();
        // There is only one outcome
        assert true;
    }
}

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateBatchUpdateTest {

    @Test
    public void testBatchUpdateWithParameterizedPreparedStatementSetter() {
        // Mock JdbcTemplate
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        // Mock data
        List<Object[]> batchArgs = Arrays.asList(
                new Object[]{"John", 25},
                new Object[]{"Jane", 30},
                new Object[]{"Bob", 22}
        );

        // Create an instance of your ParameterizedPreparedStatementSetter
        ParameterizedPreparedStatementSetter<Object[]> parameterizedSetter = (ps, argument) -> {
            ps.setString(1, (String) argument[0]);
            ps.setInt(2, (int) argument[1]);
        };

        // Capture arguments passed to batchUpdate
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BatchPreparedStatementSetter> setterCaptor = ArgumentCaptor.forClass(BatchPreparedStatementSetter.class);

        // Perform the batch update
        YourRepositoryClass repository = new YourRepositoryClass(jdbcTemplate);
        repository.batchUpdateUsingParameterizedSetter("INSERT INTO your_table (name, age) VALUES (?, ?)", batchArgs, parameterizedSetter);

        // Verify that batchUpdate is called with the correct arguments
        verify(jdbcTemplate).batchUpdate(sqlCaptor.capture(), setterCaptor.capture());

        // Assert that the SQL is correct
        assertEquals("INSERT INTO your_table (name, age) VALUES (?, ?)", sqlCaptor.getValue());

        // Assert that the BatchPreparedStatementSetter is invoked with the correct arguments
        BatchPreparedStatementSetter capturedSetter = setterCaptor.getValue();
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        // Call the setter with the mock PreparedStatement
        capturedSetter.setValues(mockPreparedStatement, 0);

        // Verify that the setter is called with the correct values
        verify(mockPreparedStatement).setString(eq(1), eq("John"));
        verify(mockPreparedStatement).setInt(eq(2), eq(25));

        // Similar verifications for other batch arguments

        // You can add more assertions as needed based on your use case
    }

    // Your repository class that uses JdbcTemplate
    private static class YourRepositoryClass {

        private final JdbcTemplate jdbcTemplate;

        public YourRepositoryClass(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        public void batchUpdateUsingParameterizedSetter(String sql, List<Object[]> batchArgs, ParameterizedPreparedStatementSetter<Object[]> parameterizedSetter) {
            jdbcTemplate.batchUpdate(sql, batchArgs, batchArgs.size(), parameterizedSetter);
        }
    }
}

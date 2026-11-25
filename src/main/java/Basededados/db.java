package Basededados;

import com.mycompany.digitalconsultant.database.ActionTrackingService;
import com.mycompany.digitalconsultant.database.CompanyProfileRepository;
import com.mycompany.digitalconsultant.database.ConsultationHistoryRepository;
import com.mycompany.digitalconsultant.database.DatabaseManager;
import com.mycompany.digitalconsultant.database.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fachada de conveniência e compatibilidade com o NetBeans e código legado.
 */
public class db {

    public static Connection getConnection() throws SQLException {
        return DatabaseManager.getInstance().getConnection();
    }

    public static UserRepository users() {
        return new UserRepository();
    }

    public static CompanyProfileRepository companies() {
        return new CompanyProfileRepository();
    }

    public static ConsultationHistoryRepository consultations() {
        return new ConsultationHistoryRepository();
    }

    public static ActionTrackingService actions() {
        return new ActionTrackingService();
    }
}

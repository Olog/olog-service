package edu.msu.nscl.olog;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * Container class for a MyBatis session using the appropriate environment and configuration
 * 
 * @author gaul
 */
public class MyBatisSession {
    
    private static SqlSessionFactory ssf;
    private static String config = "mybatis.xml";
    
    static SqlSessionFactory getSessionFactory() {
        Reader r = null;
        try {
            r = Resources.getResourceAsReader(config);
        } catch (IOException ex) {
            Logger.getLogger(MyBatisSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        ssf = new SqlSessionFactoryBuilder().build(r);
        return ssf;
    }
}


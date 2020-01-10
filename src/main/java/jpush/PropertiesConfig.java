package jpush;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesConfig {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfig.class);
    private static final Map<String,Object> propertieMap = new HashMap<String,Object> ();
    private PropertiesConfig() {}  
    private static PropertiesConfig single=null;  
    //静态工厂方法   
    public static PropertiesConfig getInstance() {
         if (single == null) {
             single = new PropertiesConfig();  
         }    
        return single;  
    }  
    
    public void initPropertiesConfig(String path){
        Properties prop = new Properties();
        InputStream in;
        try {
            in = new FileInputStream (new File(path));
            prop.load(in);
            Set keyValue = prop.keySet();
            for (Iterator it = keyValue.iterator(); it.hasNext();){
                String key = (String) it.next();
                String value = prop.getProperty(key).trim(); 
                propertieMap.put (key, value);
            }
        } catch (FileNotFoundException e) {
            logger.error ("init propertes file  error :", e);
        } catch (IOException e) {
            logger.error ("init propertes file  error :", e);
        }

    }
    
    public static final String getValueByKey(String key){
        return (String) propertieMap.get (key);
    }
}
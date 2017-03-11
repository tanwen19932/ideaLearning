package create._3_Singleton;


public class CatTest {  
  
    private static CatTest mimi = null;  
  
    private CatTest() {  
    }  
  
    private static synchronized void syncInit() {  
        if (mimi == null) {  
            mimi = new CatTest();  
        }  
    }
  
    public static CatTest getInstance() {  
        if (mimi == null) {  
            syncInit();  
        }  
        return mimi;  
    }  
}  
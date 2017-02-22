/*
 * to convert from unicode text strings to bytes needed for ESC/POS
 */

package proffittcenter;

/**
 *
 * @author Dave
 * 
 */
public class UnicodeConversion {
    
    public final int USA = 0;
    public final int FRANCE = 1;
    public final int GERMANY = 2;
    public final int UK = 3;
    public final int DENMARK1 = 4;
    public final int SWEDEN = 5;
    public final int ITALY = 6;
    public final int SPAIN = 7;
    public final int JAPAN = 8;
    public final int NORWAY = 9;
    public final int DENMARK2 = 10;
    public final int SLAVONIC = 11;
    public final int RUSSIA = 12;
    public final int PORTUGESE = 13;
    public final int GREEK = 14; 
        char[] charset={0x23,0x24,0x40,0x5b,0x5c,0x5d,0x5e,0x60,0x7b,0x7c,0x7d,0x7e};
        char[] usa={'#','$','@','[','\\',']','^','`','{','|','}','~'};
        char[] france ={'#','$','à','°','ç','§','^','`','é','ù','è','¨'};
        char[] germany = {};
        char[] uk = {'£','$','_','[','\\',']','^','`','{','|','}','~'};
        char[] denmark1 = {};
        char[] sweden = {};
        char[] italy = {};
        char[] spain = {};
        char[] japan = {};
        char[] norway = {};
        char[] denmark2 = {};
        char[] slavonic = {};
        char[] russia = {};
        char[] portugese = {};
        char[] greek = {};
        
    public byte[] unicodeToAscii(String s,
            int intenationalCharacterSet,
            int codePage){       
        byte[] result = new byte[s.length()];
        switch(intenationalCharacterSet){        
            case 0: //USA
                for(int i=0;i<charset.length;i++){
                    s=s.replace(usa[i], charset[i]);
                }
                break;       
            case 1: //FRANCE
                for(int i=0;i<charset.length;i++){
                    s=s.replace(france[i], charset[i]);
                }
                break;       
            case 2: //GERMANY
//                for(int i=0;i<charset.length;i++){
//                    s=s.replace(germany[i], charset[i]);
//                }
                break;       
            case 3: //UK
                for(int i=0;i<charset.length;i++){
                    s=s.replace(uk[i], charset[i]);
                }
                break;       
            case 4: //DENMARK1
                for(int i=0;i<charset.length;i++){
                    s=s.replace(denmark1[i], charset[i]);
                }
                break;       
            case 5: //SWEDEN
                for(int i=0;i<charset.length;i++){
                    s=s.replace(sweden[i], charset[i]);
                }
                break;       
            case 6: //ITALY
                for(int i=0;i<charset.length;i++){
                    s=s.replace(italy[i], charset[i]);
                }
                break;       
            case 7: //SPAIN
                for(int i=0;i<charset.length;i++){
                    s=s.replace(spain[i], charset[i]);
                }
                break;       
            case 8: //JAPAN
                for(int i=0;i<charset.length;i++){
                    s=s.replace(japan[i], charset[i]);
                }
                break;       
            case 9: //NORWAY
                for(int i=0;i<charset.length;i++){
                    s=s.replace(norway[i], charset[i]);
                }
                break;       
            case 10: //DENMARK2
                for(int i=0;i<charset.length;i++){
                    s=s.replace(denmark2[i], charset[i]);
                }
                break;       
            case 11: //SLAVONIC
                for(int i=0;i<charset.length;i++){
                    s=s.replace(slavonic[i], charset[i]);
                }
                break;       
            case 12: //RUSSIA
                for(int i=0;i<charset.length;i++){
                    s=s.replace(russia[i], charset[i]);
                }
                break;       
            case 13: //FRANCE
                for(int i=0;i<charset.length;i++){
                    s=s.replace(france[i], charset[i]);
                }
                break;       
            case 14: //FRANCE
                for(int i=0;i<charset.length;i++){
                    s=s.replace(france[i], charset[i]);
                }
                break;
        }
       return result;
    }

}

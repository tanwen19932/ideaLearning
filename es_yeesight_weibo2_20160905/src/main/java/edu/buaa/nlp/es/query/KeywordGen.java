package edu.buaa.nlp.es.query;

import edu.buaa.nlp.es.exception.QueryFormatException;
import edu.buaa.nlp.es.util.CharUtil;
import edu.buaa.nlp.es.util.Constant;
import edu.buaa.nlp.es.util.PingyinTool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TW
 * @date TW on 2016/10/14.
 */
public class KeywordGen {
    private static Pattern patYinHao = Pattern.compile( "(\"[^\"]+\")" );
    private static String yinhaoTag = "YH_"; //引号标签
    //for field搜索
    private static Map<String, String> hashFieldKeywords = null;    //存储可用于域搜索的关键词
    private static PingyinTool pingyinTool = null;
    //for sensitive
    private static Map<String, Integer> hashLeaders = null;
    private static Map<String, Integer> hashSensiWords = null;
    private static Map<String, Integer> hashLeadersPingyin = null;
    private static Map<String, Integer> hashSensiWordsPingyin = null;
    private boolean handledSensitiveWords = false;
    static final String senstiveLeadersFilePath = "data/sensitive/leaders.txt";
    static final  String senstiveWordsFilePath = "data/sensitive/sensiwords.txt";

    static {
        pingyinTool = new PingyinTool();
        initSensitiveModels(senstiveLeadersFilePath,senstiveWordsFilePath);
        hashFieldKeywords = new HashMap<String, String>();
        hashFieldKeywords.put( "title", "titleZh:<value> or titleEn:<value> or titleSrc:<value>" );
        hashFieldKeywords.put( "titlezh", "titleZh" );
        hashFieldKeywords.put( "titleen", "titleEn" );
        hashFieldKeywords.put( "titlesrc", "titleSrc" );
        hashFieldKeywords.put( "text", "textSrc:<value> or textEn:<value> or textZh:<value>" );
        hashFieldKeywords.put( "textsrc", "textSrc" );
        hashFieldKeywords.put( "texten", "textEn" );
        hashFieldKeywords.put( "textzh", "textZh" );
    }

    public String initKeyword(String keyword)
            throws QueryFormatException {
        //TODO 需进一步提高容错性

        //把 引号的独立出来作为一个词
        Matcher matYinhao = patYinHao.matcher( keyword );
        int index = 0;
        Map<String, String> hashYinhao = new HashMap<String, String>();
        while (matYinhao.find()) {
            ++index;
            String yinhaoGroup = matYinhao.group( 1 ).replaceAll( "^\"","" ).replaceAll( "\"$","" );
            hashYinhao.put( yinhaoTag + index, yinhaoGroup );
        }
        for (String key : hashYinhao.keySet()) {
            keyword = keyword.replace( hashYinhao.get( key ), key );
        }
        keyword = checkBracketByRegexList( keyword );
        keyword = keyword.trim();
        keyword = keyword.replaceAll( "((and|not|or)\\s*)+\\s*\\(\\s*\\)", "" );
        keyword = keyword.replaceAll( "((and|not|or)\\s*)+\\s*\\(\"\\s*\"\\)", "" );
        keyword = keyword.replaceAll( "and\\s*\\(\"\\s*\"\\)", "" );
        keyword = keyword.replaceAll( "and\\s+not\\s*\\(\\s*\\)", "" );
        keyword = keyword.replaceAll( "and\\s*\\(\\s*\\)", "" );
        keyword = keyword.replaceAll( "\\(\\s*\\)", "" );
        keyword = keyword.replaceAll( "(（|\\()", " ( " )
                .replaceAll( "(）|\\))", " ) " )
                .replaceAll( "“", " \" " )
                .replaceAll( "”", " \" " )
                .trim();


        keyword = keyword.replaceAll( "\\s+", " " );
        keyword = keyword.replaceAll( "(and|AND)\\s+(not|NOT)", "_AND_NOT_" );
        keyword = keyword.replaceAll( "and not", " " );
        keyword = keyword.replaceAll( "\\s+(or|OR)\\s+", Constant.QUERY_OPERATOR_OR2 );
        keyword = keyword.replaceAll( "\\s+(and|AND)\\s+", Constant.QUERY_OPERATOR_AND2 );
        keyword = keyword.replaceAll( "\\s+(not|NOT)\\s+", Constant.QUERY_OPERATOR_NOT2 );

        keyword = keyword.replaceAll( "\\(", " ( " )
                .replaceAll( "\\s*\\(\\s*", "_(_" )
                .replaceAll( "\\s*\\)\\s*", "_)_" )
                .trim();

        keyword = keyword.replace( " ", Constant.QUERY_OPERATOR_AND )
                .replace( "&", Constant.QUERY_OPERATOR_AND )
                .replace( "|", Constant.QUERY_OPERATOR_OR )
                .replace( "~", Constant.QUERY_OPERATOR_NOT )
        ;

        keyword = keyword.replaceAll( "_AND_NOT_", " and not " );
        keyword = keyword.replaceAll( Constant.QUERY_OPERATOR_OR2, " " + Constant.QUERY_OPERATOR_OR + " " );
        keyword = keyword.replaceAll( Constant.QUERY_OPERATOR_AND2, " " + Constant.QUERY_OPERATOR_AND + " " );
        keyword = keyword.replaceAll( Constant.QUERY_OPERATOR_NOT2, " " + Constant.QUERY_OPERATOR_NOT + " " );


        keyword = keyword.replace( "_(_", " ( " );
        keyword = keyword.replace( "_)_", " ) " );

        String[] items = keyword.split( "\\s+" );
        keyword = "";

        for (String item : items) {
            if (item.trim().isEmpty()) continue;
            if (item.trim().equalsIgnoreCase( "and" )
                    || item.equalsIgnoreCase( "not" )
                    || item.equalsIgnoreCase( "or" )
                    || item.equalsIgnoreCase( "(" )
                    || item.equalsIgnoreCase( ")" )
                    ) {
                keyword += item + " ";
            } else if (item.startsWith( "\"" ) && item.endsWith( "\"" )) {
                keyword += item + " ";
            } else {
                //对于每个单元，都是用 引号 强括号
                if (item.contains( ":" )) {
                    String[] subItem = item.split( ":" );
                    if (subItem.length == 2) {
                        if (hashFieldKeywords.containsKey( subItem[0].toLowerCase() )) {
                            String value = subItem[1];
                            if (value.startsWith( "\"" ) && item.endsWith( "\"" )) {
                                ;
                            } else {
                                value = "" + value + "";
                            }
                            String field = hashFieldKeywords.get( subItem[0].toLowerCase() );
                            if (field.contains( "or" )) {
                                keyword = " " + field.replaceAll( "<value>", value ) + " ";
                            } else {
                                keyword = " " + field + ":" + value + " ";
                            }

                        } else {
                            keyword += " " + item + " ";
                        }
                    } else {
                        keyword += " " + item + " ";
                    }
                } else {

                    keyword += " " + item + " ";
                }
            }

        }

        keyword = keyword.replaceAll( "(AND\\s+OR\\s+)+", " OR " );
        keyword = keyword.replaceAll( "(OR\\s+AND\\s+)+", " OR " );
        keyword = keyword.replaceAll( "(AND\\s+NOT\\s+)+", " NOT " );
        keyword = keyword.replaceAll( "(NOT\\s+AND\\s+)+", " NOT " );
        keyword = keyword.replaceAll( "(OR\\s+)+", " OR " );
        keyword = keyword.replaceAll( "(NOT\\s+)+", " NOT " );
        keyword = keyword.replaceAll( "(AND\\s+)+", " AND " );
        keyword = keyword.replaceAll( "\\s+", " " );

        keyword = keyword.trim();

        for (String key : hashYinhao.keySet()) {
            keyword = keyword.replace( "\"" + key + "\"", hashYinhao.get( key ) );
            keyword = keyword.replace( key + "(?=^\\d)", hashYinhao.get( key ) );
        }
        return keyword;
    }

    private String checkBracketByRegexList(String keyword) {
        List<String> regexes = new ArrayList<>();
        regexes.add( "(and|not|or)\\s*\\(\\s*\\)" );
        regexes.add( "(and|not|or)\\s*\\s*\\(\"\\s*\"\\)" );
        regexes.add( "and\\s*\\(\"\\s*\\)" );
        regexes.add( "and\\s+not\\s*\\(\\s*\\)" );
        regexes.add( "and\\s*\\(\\s*\\)" );
        regexes.add( "\\(\\s*\\)" );
        while (true) {
            String temp = keyword;
            for (String tempRegex : regexes) {
                try {
                    keyword = checkBracket( tempRegex, keyword );
                } catch (Exception e) {
                    System.out.println( tempRegex );
                    e.printStackTrace();

                }
            }
            if (keyword.equals( temp )) {
                break;
            }
        }
        return keyword;
    }

    private String checkBracket(String regex, String keyword) {
        Pattern pattern = Pattern.compile( regex );
        Matcher matcher = pattern.matcher( keyword );
        if (matcher.find()) {
            keyword = matcher.replaceAll( "" );
        }
        return keyword;
    }

    // for sensitive
    public static boolean initSensitiveModels(String leadersFile, String sensiWordsFile) {
        try {
            hashLeaders = new HashMap<String, Integer>();
            hashLeadersPingyin = new HashMap<String, Integer>();
            InputStreamReader isR = new InputStreamReader( new FileInputStream( leadersFile ), "utf-8" );
            BufferedReader br = new BufferedReader( isR );

            String line = "";
            String clearLine = "";
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                clearLine = CharUtil.ToDBC( line );
                clearLine = CharUtil.removeUnChar( clearLine );
                if (clearLine.length() > 1) {
                    clearLine = pingyinTool.toPinYin( clearLine, "", PingyinTool.Type.LOWERCASE ).toLowerCase();
                    hashLeadersPingyin.put( clearLine, 1 );
                }
                hashLeaders.put( line.trim().toLowerCase(), 1 );

            }
            br.close();
            isR.close();


            hashSensiWords = new HashMap<String, Integer>();
            hashSensiWordsPingyin = new HashMap<String, Integer>();
            isR = new InputStreamReader( new FileInputStream( sensiWordsFile ), "utf-8" );
            br = new BufferedReader( isR );

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                clearLine = CharUtil.ToDBC( line );
                clearLine = CharUtil.removeUnChar( clearLine );

                if (clearLine.length() > 1) {
                    clearLine = pingyinTool.toPinYin( clearLine, "", PingyinTool.Type.LOWERCASE ).toLowerCase();
                    hashSensiWordsPingyin.put( clearLine, 1 );
                }
                hashSensiWords.put( line.trim().toLowerCase(), 1 );
            }
            br.close();
            isR.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean addLeader(String word) {
        try {
            if (hashLeaders == null) {
                hashLeaders = new HashMap<String, Integer>();
                hashLeadersPingyin = new HashMap<String, Integer>();
            }
            hashLeaders.put( word.trim().toLowerCase(), 1 );
            word = CharUtil.ToDBC( word );
            word = CharUtil.removeUnChar( word );

            if (word.length() > 1) {
                word = pingyinTool.toPinYin( word, "", PingyinTool.Type.LOWERCASE ).toLowerCase();
                hashLeadersPingyin.put( word, 1 );
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addSensiWords(String word) {
        try {
            if (hashSensiWords == null) {
                hashSensiWords = new HashMap<String, Integer>();
                hashSensiWordsPingyin = new HashMap<String, Integer>();
            }
            hashSensiWords.put( word.trim().toLowerCase(), 1 );
            word = CharUtil.ToDBC( word );
            word = CharUtil.removeUnChar( word );

            if (word.length() > 1) {
                word = pingyinTool.toPinYin( word, "", PingyinTool.Type.LOWERCASE ).toLowerCase();
                hashSensiWordsPingyin.put( word, 1 );
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int handleSensitiveWords(String word) {
        try {
            String clearword = CharUtil.ToDBC( word );
            clearword = CharUtil.removeUnChar( clearword ).toLowerCase();


            if (clearword.length() > 1) {

                String wordPingyin = pingyinTool.toPinYin( clearword, "", PingyinTool.Type.LOWERCASE );

                if (hashLeadersPingyin != null) {
                    for (String key : hashLeadersPingyin.keySet()) {
                        String change = wordPingyin.replaceAll( "[^\\s]"+  key +"[\\s$]" ,"" );
                        if (!wordPingyin.equals( change )) {
                            return 1;
                        }
                    }
                }

                if (hashSensiWordsPingyin != null) {
                    for (String key : hashSensiWordsPingyin.keySet()) {
                        if (wordPingyin.contains( key )) {
                            return 1;
                        }
                    }
                }
            }

            if (hashLeaders != null) {
                for (String key : hashLeaders.keySet()) {
                    if (word.contains( key )) {
                        return 1;
                    }
                }
            }

            if (hashSensiWords != null) {
                for (String key : hashSensiWords.keySet()) {
                    if (word.contains( key )) {
                        return 1;
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

}

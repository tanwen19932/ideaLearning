package guava;

import com.google.common.io.Files;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import edu.buaa.nlp.tw.common.HtmlUtil;
import edu.buaa.nlp.tw.common.HttpUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TW
 * @date TW on 2016/11/11.
 */
public class GuavaFile {
    public static void main(String[] args) {
        //load all languages:
        List<LanguageProfile> languageProfiles = null;
        try {
            languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        } catch (IOException e) {
            e.printStackTrace();
        }

//build language detector:
//        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
//                .withProfiles(languageProfiles)
//                .build();

//create a text object factory
//        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forIndexingCleanText();

//query:
//        TextObject textObject = textObjectFactory.forText("my text");
//        Optional<LdLocale> lang = languageDetector.detect(textObject);
//        System.out.println(lang.get().toString());
        File dir = new File("D:\\ja");
        File[] files = dir.listFiles();
        try {
            for (File inputFiles : files) {
                for (String line : Files.readLines(inputFiles, Charset.forName("utf-8"))) {
                    Map preJo = new HashMap();
                    preJo.put("text", HtmlUtil.htmlRemoveTag(line ));
                    String jaLine = HttpUtil.doGet("http://localhost:8080/token" , preJo);
                    JSONObject jsonObject = new JSONObject(jaLine);
                    String out = jsonObject.getString("tgtl");
                    System.out.println(out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

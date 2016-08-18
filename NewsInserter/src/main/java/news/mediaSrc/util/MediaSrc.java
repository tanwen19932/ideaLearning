package news.mediaSrc.util;

public class MediaSrc {
    String mediaNameZh;
    String mediaNameEn;
    String mediaNameSrc;
    String countryNameZh;
    String countryNameEn;
    String districtNameZh;
    String districtNameEn;
    String domainName;
    String languageCode;
    String languageTname;
    String mediaLevel;
    String field = "null";

    public MediaSrc() {
        // TODO Auto-generated constructor stub
    }

    public MediaSrc(String mediaNameZh, String mediaNameEn, String mediaNameSrc, String countryNameZh,
                    String countryNameEn, String districtNameZh, String districtNameEn, String domainName, String languageCode, String languageTname, String mediaLevel) {
        super();

        this.mediaNameZh = mediaNameZh;
        this.mediaNameEn = mediaNameEn;
        this.mediaNameSrc = mediaNameSrc;
        this.countryNameZh = countryNameZh;
        this.countryNameEn = countryNameEn;
        this.districtNameZh = districtNameZh;
        this.districtNameEn = districtNameEn;
        this.domainName = domainName;
        this.languageCode = languageCode;
        this.languageTname = languageTname;
        this.mediaLevel = mediaLevel;

    }

    public String getDistrictNameZh() {
        return districtNameZh;
    }

    public void setDistrictNameZh(String districtNameZh) {
        if (districtNameZh == null)
            districtNameZh = "null";
        this.districtNameZh = districtNameZh;
    }

    public String getDistrictNameEn() {
        return districtNameEn;
    }

    public void setDistrictNameEn(String districtNameEn) {
        if (districtNameEn == null)
            districtNameEn = "null";
        this.districtNameEn = districtNameEn;
    }

    public String getMediaNameZh() {
        return mediaNameZh;
    }

    public void setMediaNameZh(String mediaNameZh) {
        if (mediaNameZh == null)
            mediaNameZh = "null";
        this.mediaNameZh = mediaNameZh;
    }

    public String getMediaNameEn() {
        return mediaNameEn;
    }

    public void setMediaNameEn(String mediaNameEn) {
        if (mediaNameEn == null)
            mediaNameEn = "null";
        this.mediaNameEn = mediaNameEn;
    }

    public String getMediaNameSrc() {
        return mediaNameSrc;
    }

    public void setMediaNameSrc(String mediaNameSrc) {
        if (mediaNameSrc == null)
            mediaNameSrc = "null";
        this.mediaNameSrc = mediaNameSrc;
    }

    public String getCountryNameZh() {
        return countryNameZh;
    }

    public void setCountryNameZh(String countryNameZh) {
        if (countryNameZh == null)
            countryNameZh = "null";
        this.countryNameZh = countryNameZh;
    }

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        if (countryNameEn == null)
            countryNameEn = "null";
        this.countryNameEn = countryNameEn;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        if (domainName == null)
            domainName = "null";
        this.domainName = domainName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        if (languageCode == null)
            languageCode = "null";
        this.languageCode = languageCode;
    }

    public String getLanguageTname() {
        return languageTname;
    }

    public void setLanguageTname(String languageTname) {
        if (languageTname == null)
            languageTname = "null";
        this.languageTname = languageTname;
    }

    public String getMediaLevel() {
        return mediaLevel;
    }

    public void setMediaLevel(String mediaLevel) {
        if (mediaLevel == null)
            mediaLevel = "null";
        this.mediaLevel = mediaLevel;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String print() {
        return this.mediaNameZh + '_' + this.countryNameZh + '_' + this.languageTname + '_' + this.domainName + '_' + this.mediaLevel;
    }

}

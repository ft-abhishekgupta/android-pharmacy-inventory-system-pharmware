package com.android.pharmware;

/**
 * Created by corei3 on 04-07-2017.
 */

public class Modeltrans {

    private String gdate;
    private String gdoc;
    private String gitems;
    private String gpt;
    private String gtotamt;
    private String gtranid;
    private String key;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    private String age;

    public String getGdate() {
        return gdate;
    }

    public void setGdate(String gdate) {
        this.gdate = gdate;
    }

    public String getGdoc() {
        return gdoc;
    }

    public void setGdoc(String gdoc) {
        this.gdoc = gdoc;
    }

    public String getGitems() {
        return gitems;
    }

    public void setGitems(String gitems) {
        this.gitems = gitems;
    }

    public String getGpt() {
        return gpt;
    }

    public void setGpt(String gpt) {
        this.gpt = gpt;
    }

    public String getGtotamt() {
        return gtotamt;
    }

    public void setGtotamt(String gtotamt) {
        this.gtotamt = gtotamt;
    }

    public String getGtranid() {
        return gtranid;
    }

    public void setGtranid(String gtranid) {
        this.gtranid = gtranid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Modeltrans() {

    }

    public Modeltrans(String gdate, String gdoc, String gitems, String gpt, String gtotamt, String gtranid, String key,
            String age) {
        this.gdate = gdate;
        this.gdoc = gdoc;
        this.gitems = gitems;
        this.gpt = gpt;
        this.gtotamt = gtotamt;
        this.gtranid = gtranid;
        this.key = key;
        this.age = age;
    }

}

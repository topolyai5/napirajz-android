package hu.napirajz.android.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NapirajzData {

    @SerializedName("ID")
    private String id;
    @SerializedName("Cim")
    private String cim;
//    @SerializedName("Datum")
//    private Date datum;
    @SerializedName("URL")
    private String url;
    @SerializedName("LapURL")
    private String lapUrl;
    @SerializedName("Parbeszed")
    private String parbeszed;
    @SerializedName("Egyeb")
    private String egyeb;
}

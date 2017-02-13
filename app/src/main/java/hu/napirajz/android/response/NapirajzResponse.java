package hu.napirajz.android.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true)
public class NapirajzResponse {

    private NapirajzData data;
}

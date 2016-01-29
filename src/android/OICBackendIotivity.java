package com.intel.cordova.plugin.oic;

// Java
import java.util.EnumSet;
import java.util.List;

// Cordova
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

// Android
import android.util.Log;

// Iotivity
import org.iotivity.base.OcConnectivityType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcResource;
import org.iotivity.base.QualityOfService;

// Third party
import org.json.JSONArray;
import org.json.JSONException;


public class OICBackendIotivity
    implements OICBackendInterface,
               OcPlatform.OnResourceFoundListener
{
    private CallbackContext callbackContext;

    public void onResourceFound(OcResource resource) {
        String deviceId = resource.getHost();
        String resourcePath = resource.getUri();
        List<String> resourceTypes = resource.getResourceTypes();
        String resourceType = "";

        if (resourceTypes.size() > 0) {
            resourceType = resourceTypes.get(0);
        }

        OICResource oicResource = new OICResource(deviceId, resourcePath, resourceType);
        OICResourceEvent ev = new OICResourceEvent(oicResource);

        try {
            PluginResult result = new PluginResult(PluginResult.Status.OK, ev.toJSON());
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        } catch (JSONException ex) {
            this.callbackContext.error(ex.getMessage());
        }
    }

    public void findResources(JSONArray args, CallbackContext cc)
        throws JSONException
    {
        String host = args.getJSONObject(0).getString("deviceId");
        String resourceUri = args.getJSONObject(0).getString("resourcePath");

        this.callbackContext = cc;

        try {
            OcPlatform.findResource(
                host, resourceUri,
                EnumSet.of(OcConnectivityType.CT_DEFAULT),
                this);
        } catch (OcException ex) {
            this.callbackContext.error(ex.getMessage());
        }
    }
}

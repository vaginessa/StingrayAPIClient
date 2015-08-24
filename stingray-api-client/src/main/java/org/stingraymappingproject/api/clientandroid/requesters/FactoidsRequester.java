package org.stingraymappingproject.api.clientandroid.requesters;

import com.android.volley.Request;

import org.json.JSONObject;
import org.stingraymappingproject.api.clientandroid.ClientService;
import org.stingraymappingproject.api.clientandroid.models.Factoid;
import org.stingraymappingproject.api.clientandroid.GsonRequest;

/**
 * Created by Marvin Arnold on 23/08/15.
 */
public abstract class FactoidsRequester extends Requester<Factoid[]> {
    public FactoidsRequester(ClientService clientService) {
        super(clientService);
    }

    @Override
    protected JSONObject getJSONObjectParameters() {
        return null;
    }

    @Override
    protected GsonRequest<Factoid[]> getRequest() {
        return getRequest("factoids", Request.Method.GET, Factoid[].class);
    }
}
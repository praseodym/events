package ch.wisv.events.tickets.service;

import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.model.order.Order;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@Validated
@Service
public class PaymentsServiceImpl implements PaymentsService {

    /**
     * Field issuerUri
     */
    @Value("${wisvch.payments.issuerUri}")
    @NotNull
    private String issuerUri;

    /**
     * Field clientUri
     */
    @Value("${wisvch.payments.clientUri}")
    @NotNull
    private String clientUri;

    /**
     * Field httpClient
     */
    private HttpClient httpClient;

    /**
     * Default constructor.
     */
    public PaymentsServiceImpl() {
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Constructor with own HttpClient.
     *
     * @param httpClient of type HttpClient
     */
    public PaymentsServiceImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Get the Order status of Payments.
     *
     * @param paymentsReference of type String
     * @return String
     */
    @Override
    public String getPaymentsOrderStatus(String paymentsReference) {
        try {
            HttpGet httpGet = new HttpGet(issuerUri + "/api/orders/" + paymentsReference);
            httpGet.setHeader("Accept", "application/json");

            HttpResponse response = this.httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                JSONObject responseObject = (JSONObject) JSONValue.parse(EntityUtils.toString(entity));

                if (responseObject.containsKey("status")) {
                    return (String) responseObject.get("status");
                }
            }
        } catch (IOException ignored) {
        }

        throw new PaymentsConnectionException();
    }

    /**
     * Get a Mollie Url via Payments.
     *
     * @param order of type Order
     * @return String
     */
    @Override
    public String getPaymentsMollieUrl(Order order) {
        try {
            HttpPost httpPost = this.createPaymentsOrderHttpPost(order);

            HttpResponse response = this.httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                JSONObject responseObject = (JSONObject) JSONValue.parse(EntityUtils.toString(response.getEntity()));

                if (responseObject.containsKey("url")) {
                    return (String) responseObject.get("url");
                }
            }
        } catch (IOException ignored) {
        }

        throw new PaymentsConnectionException();
    }

    /**
     * Create a HttpPost to create a Payments Order request
     *
     * @param order of type Order
     * @return HttpPost
     */
    public HttpPost createPaymentsOrderHttpPost(Order order) {
        HttpPost httpPost = new HttpPost(issuerUri + "/api/orders");

        JSONObject object = new JSONObject();
        object.put("name", order.getCustomer().getName());
        object.put("email", order.getCustomer().getEmail());
        object.put("returnUrl", clientUri + "/status/" + order.getPublicReference() + "/");
        object.put("mailConfirmation", false);

        JSONArray jsonArray = new JSONArray();
        order.getOrderProducts().forEach(orderProduct -> {
            for (int i = 0; i < orderProduct.getAmount(); i++) {
                jsonArray.add(orderProduct.getProduct().getKey());
            }
        });
        object.put("productKeys", jsonArray);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(object.toJSONString(), "UTF8"));
        return httpPost;
    }
}

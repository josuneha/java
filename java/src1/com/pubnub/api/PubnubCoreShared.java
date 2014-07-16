package com.pubnub.api;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.pubnub.api.PubnubError.*;

/**
 * Pubnub object facilitates querying channels for messages and listening on
 * channels for presence/message events
 *
 * @author Pubnub
 *
 */

abstract class PubnubCoreShared extends PubnubCore {

    /**
     * Pubnub Constructor
     *
     * @param publish_key
     *            Publish Key
     * @param subscribe_key
     *            Subscribe Key
     * @param secret_key
     *            Secret Key
     * @param cipher_key
     *            Cipher Key
     * @param ssl_on
     *            SSL on ?
     */
    public PubnubCoreShared(String publish_key, String subscribe_key, String secret_key,
                  String cipher_key, boolean ssl_on) {
        super(publish_key, subscribe_key, secret_key, cipher_key, ssl_on);
    }

    /**
     * Pubnub Constructor
     *
     * @param publish_key
     *            Publish key
     * @param subscribe_key
     *            Subscribe Key
     * @param secret_key
     *            Secret Key
     * @param ssl_on
     *            SSL on ?
     */
    public PubnubCoreShared(String publish_key, String subscribe_key, String secret_key,
                  boolean ssl_on) {
        super(publish_key, subscribe_key, secret_key, "", ssl_on);
    }

    /**
     * Pubnub Constructor
     *
     * @param publish_key
     *            Publish Key
     * @param subscribe_key
     *            Subscribe Key
     */
    public PubnubCoreShared(String publish_key, String subscribe_key) {
        super(publish_key, subscribe_key, "", "", false);
    }

    /**
     * @param publish_key
     *            Publish Key
     * @param subscribe_key
     *            Subscribe Key
     * @param ssl
     */
    public PubnubCoreShared(String publish_key, String subscribe_key, boolean ssl) {
        super(publish_key, subscribe_key, "", "", ssl);
    }

    /**
     * @param publish_key
     * @param subscribe_key
     * @param secret_key
     */
    public PubnubCoreShared(String publish_key, String subscribe_key, String secret_key) {
        super(publish_key, subscribe_key, secret_key, "", false);
    }


    /**
    *
    * Constructor for Pubnub Class
    *
    * @param publish_key
    *            Publish Key
    * @param subscribe_key
    *            Subscribe Key
    * @param secret_key
    *            Secret Key
    * @param cipher_key
    *            Cipher Key
    * @param ssl_on
    *            SSL enabled ?
    * @param initialization_vector
    *            Initialization vector
    */

    public PubnubCoreShared(String publish_key, String subscribe_key,
                  String secret_key, String cipher_key, boolean ssl_on, String initialization_vector) {
        super(publish_key, subscribe_key, secret_key, cipher_key, ssl_on, initialization_vector);
    }

    /**
     * Sets value for UUID
     *
     * @param uuid
     *            UUID value for Pubnub client
     */
    public void setUUID(UUID uuid) {
        this.UUID = uuid.toString();
    }

    public String uuid() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * This method sets timeout value for subscribe/presence. Default value is
     * 310000 milliseconds i.e. 310 seconds
     *
     * @param timeout
     *            Timeout value in milliseconds for subscribe/presence
     */
    public void setSubscribeTimeout(int timeout) {
        super.setSubscribeTimeout(timeout);
    }

    /**
     * This method returns timeout value for subscribe/presence.
     *
     * @return Timeout value in milliseconds for subscribe/presence
     */
    public int getSubscribeTimeout() {
        return super.getSubscribeTimeout();
    }

    /**
     * This method set timeout value for non subscribe operations like publish,
     * history, hereNow. Default value is 15000 milliseconds i.e. 15 seconds.
     *
     * @param timeout
     *            Timeout value in milliseconds for Non subscribe operations
     *            like publish, history, hereNow
     */
    public void setNonSubscribeTimeout(int timeout) {
        super.setNonSubscribeTimeout(timeout);
    }
    /**
     * This method returns timeout value for non subscribe operations like publish, history, hereNow
     *
     * @return Timeout value in milliseconds for for Non subscribe operations like publish, history, hereNow
     */
    public int getNonSubscribeTimeout() {
        return super.getNonSubscribeTimeout();
    }

    private String pamSign(String key, String data) throws PubnubException {
        Mac sha256_HMAC;

        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(),
                    "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hmacData = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
            return new String(Base64Encoder.encode(hmacData)).replace('+', '-')
                   .replace('/', '_');
        } catch (InvalidKeyException e1) {
            throw new PubnubException(getErrorObject(PubnubError.PNERROBJ_ULSSIGN_ERROR, 1, "Invalid Key : " + e1.toString()));
        } catch (NoSuchAlgorithmException e1) {
            throw new PubnubException(getErrorObject(PubnubError.PNERROBJ_ULSSIGN_ERROR, 2, "Invalid Algorithm : " + e1.toString()));
        } catch (IllegalStateException e1) {
            throw new PubnubException(getErrorObject(PubnubError.PNERROBJ_ULSSIGN_ERROR, 3, "Invalid State : " + e1.toString()));
        } catch (UnsupportedEncodingException e1) {
            throw new PubnubException(getErrorObject(PubnubError.PNERROBJ_ULSSIGN_ERROR, 4, "Unsupported encoding : " + e1.toString()));
        }
    }

    /** Grant r/w access based on channel and auth key
     * @param channel
     * @param auth_key
     * @param read
     * @param write
     * @param callback
     */
    public void pamGrant(final String channel, String auth_key, boolean read,
                         boolean write, final Callback callback) {
        pamGrant(channel, auth_key, read, write, -1, callback);
    }

    /** Grant r/w access based on channel
     * @param channel
     * @param read
     * @param write
     * @param callback
     */
    public void pamGrant(final String channel, boolean read,
                         boolean write, final Callback callback) {
        pamGrant(channel, null, read, write, -1, callback);
    }

    /** Grant r/w access based on channel
     * @param channel
     * @param read
     * @param write
     * @param ttl
     * @param callback
     */
    public void pamGrant(final String channel, boolean read,
                         boolean write, int ttl, final Callback callback) {
        pamGrant(channel, null, read, write, ttl, callback);
    }
    
    /** Grant r/w access based on channel and auth key
     * @param channel
     * @param auth_key
     * @param read
     * @param write
     * @param ttl
     * @param callback
     */
    public void pamGrant(final String channel, String auth_key, boolean read,
                         boolean write, int ttl, Callback callback) {
    	pamGrant(channel, auth_key, null, read, write, ttl, callback);
    }

    /** Grant r/w access based on objectId
     * @param objectId
     * @param read
     * @param write
     * @param ttl
     * @param callback
     */
    public void pamGrantByObjectId(final String objectId, boolean read,
                         boolean write, int ttl, Callback callback) {
    	pamGrant(null, null, objectId, read, write, ttl, callback);
    }
    
    private void pamGrant(final String channel, String auth_key, String objectId, boolean read,
                         boolean write, int ttl, Callback callback) {
        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);

        String r = (read) ? "1" : "0";
        String w = (write) ? "1" : "0";

        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(channel,
                                   getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 1));
            return;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "grant" + "\n" ;

        if (auth_key != null && auth_key.length() > 0)
            sign_input += "auth=" + auth_key + "&"  ;

        if (channel != null && channel.length() > 0)
        	sign_input += "channel=" + channel + "&";
        
        if (objectId != null && objectId.length() > 0)
        	sign_input += "obj-id=" + objectId + "&";
        
        sign_input += 	"pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&" + 
        				"r=" + r + "&" + 
        				"timestamp=" + timestamp + ((ttl >= -1)?"&" + "ttl=" + ttl:"") + "&" + 
        				"w=" + w;


        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(channel,
                                   e1.getPubnubError());
            return;
        }


        parameters.put("w", w);
        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("r", r);

        if (auth_key != null && auth_key.length() > 0 ) parameters.put("auth", auth_key);
        if (objectId != null && objectId.length() > 0 ) parameters.put("obj-id", objectId);
        if (channel != null && channel.length() > 0 ) parameters.put("channel", channel);
        if (ttl >= -1) parameters.put("ttl", String.valueOf(ttl));

        String[] urlComponents = { getPubnubUrl(), "v1", "auth", "grant", "sub-key",
                                   this.SUBSCRIBE_KEY
                                 };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
        new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response) {
                invokeCallback(channel, response, "payload", cb, 4);
            }

            public void handleError(HttpRequest hreq, PubnubError error) {
                cb.errorCallback(channel, error);
                return;
            }
        });

        _request(hreq, nonSubscribeManager);

    }

    /** ULS Audit
     * @param callback
     */
    public void pamAudit(Callback callback) {
    	pamAudit(null, null, null, callback);
    }

    /** ULS audit by channel
     * @param channel
     * @param callback
     */
    public void pamAudit(final String channel,
                         Callback callback) {
    	pamAudit(channel, null, null, callback);
    }
    
    /** ULS audit by channel
     * @param channel
     * @param authKey
     * @param callback
     */
    public void pamAudit(final String channel, String authKey,
                         Callback callback) {
    	pamAudit(channel, authKey, null, callback);
    }

    /** ULS audit by channel and auth key
     * @param channel
     * @param auth_key
     * @param callback
     */
    private void pamAudit(final String channel, String authKey, String objectId,
                         Callback callback) {

        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);

        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(channel,
                                   getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 4));
            return;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "audit" + "\n" ;

        if (authKey != null && authKey.length() > 0)
            sign_input += "auth=" + authKey + "&"  ;

        if (channel != null && channel.length() > 0)
        	sign_input += "channel=" + channel + "&";
        
        if (objectId != null && objectId.length() > 0)
        	sign_input += "obj-id=" + objectId + "&";
        
        sign_input += 	"pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&" + 
        				"timestamp=" + timestamp;
        
        
        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(channel,
                                   e1.getPubnubError());
            return;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        if (channel != null && channel.length() > 0 ) parameters.put("channel", channel);
        if (authKey != null && channel.length() > 0 ) parameters.put("auth", authKey);
        if (objectId != null && channel.length() > 0 ) parameters.put("obj-id", objectId);

        String[] urlComponents = { getPubnubUrl(), "v1", "auth", "audit", "sub-key",
                                   this.SUBSCRIBE_KEY
                                 };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
        new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response) {
                invokeCallback(channel, response, "payload", cb, 2);
            }

            public void handleError(HttpRequest hreq, PubnubError error) {
                cb.errorCallback(channel, error);
                return;
            }
        });

        _request(hreq, nonSubscribeManager);

    }

    /** ULS revoke by channel and auth key
     * @param channel
     * @param auth_key
     * @param callback
     */
    public void pamRevoke(String channel, String auth_key, Callback callback) {
        pamGrant(channel, auth_key, false, false, callback);
    }


    /** ULS revoke by channel
     * @param channel
     * @param callback
     */
    public void pamRevoke(String channel, Callback callback) {
        pamGrant(channel, null, false, false, callback);
    }
    public void pamRevokeByObjectId(String objectId, Callback callback) {
    	pamGrantByObjectId(objectId, false, false, -1, callback);
    }
    public void pamAuditByObjectId(String objectId, Callback callback) {
    	pamAudit(null, null, objectId, callback);
    }
}

package com.mycompany.etcd.java.demo;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.jboss.logging.Logger;

@Path("v1")
public class CacheResource {
    private final Logger log = Logger.getLogger(CacheResource.class);

    @GET
    @Path("/put")
    @Produces("application/json")
    public Response put() {
        log.info("in put()");
        Client client = null;
        try {
            String url = "http://etcd:2379";
            client = Client.builder().endpoints(url).build();
            KV kvClient = client.getKVClient();
            ByteSequence key = ByteSequence.from("test_key".getBytes(StandardCharsets.UTF_8));
            ByteSequence value = ByteSequence.from("test_value".getBytes(StandardCharsets.UTF_8));
            PutResponse response = kvClient.put(key, value).get();
          
            String result = String.format("added key and value. key already existed=%b", response.hasPrevKv());
            log.info(result);
            return Response.ok(result).build();
            
        } catch (InterruptedException | ExecutionException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } finally {
            if(client!=null) {
                client.close();
            }
        }
    }

    @GET
    @Path("/get")
    @Produces("application/json")
    public Response get() {
        log.info("in get()");
        Client client = null;
        try {
            String url = "http://etcd:2379";
            client = Client.builder().endpoints(url).build();
            KV kvClient = client.getKVClient();
            ByteSequence key = ByteSequence.from("test_key".getBytes(StandardCharsets.UTF_8));
            CompletableFuture<GetResponse> getFuture = kvClient.get(key);
            GetResponse response = getFuture.get();
            if(response.getKvs().isEmpty()) {
                String result = "key not found";
                log.info(result);
                return Response.ok(result).build();
            } else {
                KeyValue keyValue = response.getKvs().get(0);
                byte[] bytes = keyValue.getValue().getBytes();
                String value = new String(bytes, StandardCharsets.UTF_8);

                String result = String.format("got %s", value);
                log.info(result);
                return Response.ok(result).build();
            }
            
        } catch (InterruptedException | ExecutionException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } finally {
            if(client!=null) {
                client.close();
            }
        }
    }
    
    @GET
    @Path("/delete")
    @Produces("application/json")
    public Response delete() {
        log.info("in delete()");
        Client client = null;
        try {
            String url = "http://etcd:2379";
            client = Client.builder().endpoints(url).build();
            KV kvClient = client.getKVClient();
            ByteSequence key = ByteSequence.from("test_key".getBytes(StandardCharsets.UTF_8));
            // delete the key
            DeleteResponse response = kvClient.delete(key).get();
            
            String result = String.format("key deleted %d%n", response.getDeleted());
            log.info(result);
            return Response.ok(result).build();
            
        } catch (InterruptedException | ExecutionException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } finally {
            if(client!=null) {
                client.close();
            }
        }
    }

}

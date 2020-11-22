package com.demo.api.developers;

import com.demo.api.developers.model.Developer;
import com.demo.api.developers.model.Skill;
import com.demo.api.developers.service.DeveloperService;
import com.demo.api.developers.service.HazelcastService;
import io.quarkus.vault.VaultTransitSecretEngine;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

@Path("/developers")
public class DevelopersResource {

    private static Logger logger = LoggerFactory.getLogger(DevelopersResource.class);

    @Inject
    DeveloperService service;

    @Inject
    HazelcastService hzService;

    @ConfigProperty(name="developer.greeting.message")
    String message;

    @ConfigProperty(name="extra.message")
    String extraMessage;

    @ConfigProperty(name="secret.password")
    String secretPassword;

    @ConfigProperty(name="foo")
    String foo;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(){
        logger.debug("In hello() method");
        return message + " -> " +extraMessage;
    }

    @GET
    @Path("/hello/secret")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloSecret(){
        return message + " -> " +secretPassword;
    }

    /*@GET
    @Path("/hello/secret/volume")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloSecretVolume() throws Exception{
        byte[] secretProperty = Files.readAllBytes(Paths.get("/mounted/secret/config/secret.password"));
        return "Secret Volume: -> " +new String(secretProperty);
    }*/

    @GET
    @Path("/hello/secret/vault")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloSecretVault() throws Exception{
        return "Secret Vault: -> " +foo;
    }

    @Inject
    VaultTransitSecretEngine transit;

    @GET
    @Path("/encrypt")
    @Produces(MediaType.TEXT_PLAIN)
    public String encrypt(@QueryParam("text") String text) {
        String encryptedString = transit.encrypt("my_encryption", text);
        logger.info("Encrypted text -> "+encryptedString);
        return encryptedString;
    }

    @GET
    @Path("/decrypt")
    @Produces(MediaType.TEXT_PLAIN)
    public String decrypt(@QueryParam("text") String text) {
        String decryptedString = transit.decrypt("my_encryption", text).asString();
        logger.info("Decrypted text -> "+decryptedString);
        return decryptedString;
    }

    @GET
    @Path("/sign")
    @Produces(MediaType.TEXT_PLAIN)
    public String sign(@QueryParam("text") String text) {
        String signedText = transit.sign("my-sign-key", text);
        logger.info("Signed text -> "+signedText);
        return signedText;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevelopers() throws SQLException {
        return Response.ok().entity(service.getDevelopers()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDeveloper(Developer developer) throws SQLException {
        service.addDeveloper(developer);

        return Response.created(UriBuilder.fromResource(DevelopersResource.class).
                path(developer.getId()).build()).entity(developer).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeveloper(@PathParam("id") String id) throws SQLException {
        return Response.ok().entity(service.getDeveloper(id)).build();
    }

    @GET
    @Path("/skills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkills() {
        return hzService.getSkills();
    }

    @POST
    @Path("/skills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSkills(Skill[] skills) {
        return hzService.addSkills(skills);
    }
}

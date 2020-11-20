package com.demo.api.developers.service;

import com.demo.api.developers.model.Developer;
import com.demo.api.developers.model.Skill;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Map;

@ApplicationScoped
public class HazelcastService {

    @Inject
    HazelcastInstance hazelcastClient;

    public Response addSkills(Skill[] skillsToAdd){
        IMap<String, String> skills = hazelcastClient.getMap("skills");

        for(Skill skillToAdd : skillsToAdd){
            skills.put(skillToAdd.getCode(), skillToAdd.getDescription());
        }

        System.err.println(
                "Known capital cities: " + skills.size());
        System.err.println(
                "Capital city of GB: " + skills.get("GB"));

        return Response.ok().entity(skillsToAdd).build();
    }

    public Response getSkills(){
        Map<String, String> skills = hazelcastClient.getMap("skills");
        return Response.ok().entity(skills).build();
    }

    public String getSkillDescription(String code){
        Map<String, String> skills = hazelcastClient.getMap("skills");
        return skills.get(code);
    }
}

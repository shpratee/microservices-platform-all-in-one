package com.demo.api.developers.service;

import com.demo.api.developers.model.Developer;
import com.demo.api.developers.model.DeveloperServer;
import com.demo.api.developers.model.Skill;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class DeveloperService {

    private static final Map<String, DeveloperServer> developers = new HashMap<>();

    @Inject
    HazelcastService hzService;

    public DeveloperServer[] getDevelopers() {
        Set keys = developers.keySet();

        List<DeveloperServer> devServerList = new ArrayList();
        Iterator keysIter = keys.iterator();
        while(keysIter.hasNext()){
            devServerList.add(developers.get(keysIter.next()));
        }

        return devServerList.toArray(new DeveloperServer[devServerList.size()]);
    }

    public DeveloperServer getDeveloper(String id) {
        return developers.get(id);
    }

    public void addDeveloper(Developer developer) {
        String[] skillStrs = developer.getSkills();
        Skill[] skills = new Skill[skillStrs.length];

        for (int i=0; i< skillStrs.length;i++){
            skills[i] = new Skill(skillStrs[i], hzService.getSkillDescription(skillStrs[i]));
        }

        DeveloperServer devServer = new DeveloperServer();
        devServer.setId(developer.getId());
        devServer.setName(developer.getName());
        devServer.setSkills(skills);

        developers.put(developer.getId(), devServer);
    }
}

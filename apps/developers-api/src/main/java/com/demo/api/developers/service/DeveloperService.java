package com.demo.api.developers.service;

import com.demo.api.developers.model.Developer;
import com.demo.api.developers.model.DeveloperServer;
import com.demo.api.developers.model.Skill;
import io.agroal.api.AgroalDataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.*;
import java.util.*;

@ApplicationScoped
public class DeveloperService {

    private static final Map<String, DeveloperServer> developers = new HashMap<>();

    @Inject
    HazelcastService hzService;

    @Inject
    AgroalDataSource dataSource;

    public DeveloperServer[] getDevelopers() throws SQLException {
//        Set keys = developers.keySet();
//
//        List<DeveloperServer> devServerList = new ArrayList();
//        Iterator keysIter = keys.iterator();
//        while(keysIter.hasNext()){
//            devServerList.add(developers.get(keysIter.next()));
//        }
//
//        return devServerList.toArray(new DeveloperServer[devServerList.size()]);
        Connection conn = dataSource.getConnection();
        String query = "SELECT * FROM developers";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        List<DeveloperServer> devServerList = new ArrayList<>();
        try {

            while (rs.next()) {
                DeveloperServer devServer = new DeveloperServer();
                devServer.setId(rs.getString("id"));
                devServer.setName(rs.getString("name"));

                List<Skill> skillsList = new ArrayList<>();
                String[] skills = rs.getString("skills").split(",");
                for (String skill : skills) {
                    Skill s = new Skill();
                    s.setCode(skill);
                    s.setDescription(hzService.getSkillDescription(skill));
                    skillsList.add(s);
                }

                devServer.setSkills(skillsList.toArray(new Skill[skillsList.size()]));

                devServerList.add(devServer);
            }
        } finally {
            rs.close();
            stmt.close();
            conn.close();
        }

        return devServerList.toArray(new DeveloperServer[devServerList.size()]);
    }

    public DeveloperServer getDeveloper(String id) throws SQLException {
        Connection conn = dataSource.getConnection();
        String query = "select * from developers where id='"+id+"'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        DeveloperServer devServer = new DeveloperServer();
        try {
            while (rs.next()) {

                devServer.setId(rs.getString("id"));
                devServer.setName(rs.getString("name"));

                List<Skill> skillsList = new ArrayList<>();
                String[] skills = rs.getString("skills").split(",");
                for (String skill : skills) {
                    Skill s = new Skill();
                    s.setCode(skill);
                    s.setDescription(hzService.getSkillDescription(skill));
                    skillsList.add(s);
                }

                devServer.setSkills(skillsList.toArray(new Skill[skillsList.size()]));
            }
        } finally {
            rs.close();
            stmt.close();
            conn.close();
        }

        return devServer;
    }

    public void addDeveloper(Developer developer) throws SQLException {
        Connection conn = dataSource.getConnection();
        PreparedStatement prepStmt = conn.prepareStatement("INSERT into developers(id, name, skills) values (?,?,?)");
        prepStmt.setString(1, developer.getId());
        prepStmt.setString(2, developer.getName());

        try{
            String[] skills = developer.getSkills();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i < skills.length; i++){
                sb.append(skills[i]);

                if(i != (skills.length - 1)){
                    sb.append(",");
                }
            }

            prepStmt.setString(3, sb.toString());

            prepStmt.execute();
        } finally {
            prepStmt.close();
            conn.close();
        }
    }
}

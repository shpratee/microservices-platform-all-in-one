package com.demo.api.developers;

import com.demo.api.developers.model.DeveloperServer;
import com.demo.api.developers.model.Skill;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class DevelopersResourceTest {

    //@Test
    public void testHelloEndpoint() {
        given()
          .when().get("/developers/hello")
          .then()
             .statusCode(200)
             .body(is("Hello All! -> Something extra?"));
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        Connection conn =
                DriverManager.getConnection("jdbc:postgresql://192.168.99.103:30682/postgres?sslMode=false&gssEncMode=false", "postgres", "puK5ZFZ5pw");

        //Connection conn = dataSource.getConnection("postgres", "puK5ZFZ5pw");
        String query = "select * from known_developers";
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
                    //s.setDescription(hzService.getSkillDescription(skill));
                    skillsList.add(s);
                }

                devServer.setSkills(skillsList.toArray(new Skill[skillsList.size()]));
            }
        } finally {
            rs.close();
            stmt.close();
            conn.close();
        }
    }

}
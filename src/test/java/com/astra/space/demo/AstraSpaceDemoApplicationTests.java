package com.astra.space.demo;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import com.astra.space.demo.dao.SessionManager;
import com.astra.space.demo.dao.SpacecraftJourneyDao;
import com.astra.space.demo.dao.SpacecraftMapper;
import com.astra.space.demo.dao.SpacecraftMapperBuilder;
import com.astra.space.demo.entity.SpacecraftJourneyCatalog;
import com.astra.space.demo.service.AstraService;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.uuid.*;


@SpringBootTest
class AstraSpaceDemoApplicationTests {
    @BeforeAll
    static void startUp() {

        Path resourceDirectory = Paths.get("src","test","resources", "secure-connect-mercury.zip");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        SessionManager.getInstance()
                .saveCredentials("rDaKfYDLwJBPsAXwkXKqEcJS",
                        "h,-qMryH0aeq.66l34s7SXPGkQFAOpLaDIvnZBq_1E0m5d1lGzGR5Cw42PjaWaQ0LvisAj_Zgg,geMpxWFpLYaaOSBA6fjr52L0Tpi1ppxwnQ0Zqja-vtv64m0B2Esld",
                        "missions",
                        absolutePath );
    }


    @Test
    void testSessionManager(){

        SessionManager.getInstance().checkConnection();
    }

    @Test
    void testRelativePathOfTest() {
        Path resourceDirectory = Paths.get("src","test","resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        System.out.println(absolutePath);

        Assertions.assertTrue(absolutePath.endsWith("resources"));
    }

    @Test
    void testInsertSpaceCraft() {

        SpacecraftMapper mapper = new SpacecraftMapperBuilder(SessionManager.getInstance().connectToAstra()).build();
        SpacecraftJourneyDao dao = mapper.spacecraftJourneyDao(CqlIdentifier.fromCql(SessionManager.getInstance().getKeySpace()));
        SpacecraftJourneyCatalog spacecraft = new SpacecraftJourneyCatalog();
        spacecraft.setName("Voyager");
        spacecraft.setSummary("The Journey of Voyager");
        spacecraft.setStart(Instant.now());
        spacecraft.setActive(true);
        spacecraft.setJourneyId(Uuids.timeBased());

        dao.upsert(spacecraft);


    }

    @Test
    void testGetAllSpaceCraftNames() {
        AstraService service = new AstraService();
        List< SpacecraftJourneyCatalog > results = service.findAllSpacecrafts();

        Assertions.assertTrue(results.size() > 0);
    }


    @AfterAll
    static void cleanUp(){
        SessionManager.getInstance().close();
    }
}

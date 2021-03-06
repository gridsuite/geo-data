/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.geodata.server;

import com.datastax.oss.driver.api.core.CqlSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powsybl.iidm.network.Country;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.network.store.client.NetworkStoreService;
import org.gridsuite.geodata.extensions.Coordinate;
import org.gridsuite.geodata.server.dto.LineGeoData;
import org.gridsuite.geodata.server.dto.SubstationGeoData;
import org.gridsuite.geodata.server.repositories.LineCustomRepository;
import org.gridsuite.geodata.server.repositories.LineRepository;
import org.gridsuite.geodata.server.repositories.SubstationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static com.powsybl.network.store.model.NetworkStoreApi.VERSION;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GeoDataController.class)
public class GeoDataControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GeoDataService geoDataService;

    @MockBean
    private CassandraConfig cassandraConfig;

    @MockBean
    private CqlSession cqlSession;

    @MockBean
    private NetworkStoreService service;

    @MockBean
    private SubstationRepository substationRepository;

    @MockBean
    private LineRepository lineRepository;

    @MockBean
    private LineCustomRepository lineCustomRepository;

    @Test
    public void test() throws Exception {
        UUID networkUuid = UUID.fromString("7928181c-7977-4592-ba19-88027e4254e4");

        given(service.getNetwork(networkUuid)).willReturn(EurostagTutorialExample1Factory.create());

        mvc.perform(get("/" + VERSION + "/substations?networkUuid=" + networkUuid)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        mvc.perform(get("/" + VERSION + "/lines?networkUuid=" + networkUuid)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        String substationJson = objectMapper.writeValueAsString(Collections.singleton(
                SubstationGeoData.builder()
                        .id("testID")
                        .country(Country.FR)
                        .coordinate(new Coordinate(1, 1))
                        .build()));

        mvc.perform(post("/" + VERSION + "/substations")
                .contentType(APPLICATION_JSON)
                .content(substationJson))
                .andExpect(status().isOk());

        mvc.perform(post("/" + VERSION + "/lines")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singleton(
                        LineGeoData.builder()
                                .country1(Country.FR)
                                .country2(Country.BE)
                                .substationStart("subFR")
                                .substationEnd("subBE")
                                .coordinates(new ArrayList<>())
                                .build()))))
                .andExpect(status().isOk());
    }
}

/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.geodata.server.repositories;

import com.powsybl.geodata.server.CassandraConfig;
import com.powsybl.geodata.server.GeoDataApplication;
import com.powsybl.geodata.server.GeoDataService;
import com.powsybl.geodata.server.dto.LineGeoData;
import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnitDependencyInjectionTestExecutionListener;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GeoDataApplication.class, CassandraConfig.class})
@TestExecutionListeners({ CassandraUnitDependencyInjectionTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class })
@CassandraDataSet(value = "geo_data.cql", keyspace = "geo_data")
@EmbeddedCassandra
public class LineCustomRepositoryTest {

    @MockBean
    GeoDataService geoDataService;

    @Autowired
    private LineCustomRepository lineCustomRepository;

    @Autowired
    private LineRepository lineRepository;

    @Test
    public void test() {

        lineRepository.save(LineEntity.builder()
                .id("testId")
                .country("FR")
                .otherCountry("BE")
                .build());

        lineRepository.save(LineEntity.builder()
                .id("testId2")
                .country("FR")
                .otherCountry("FR")
                .build());

        lineRepository.save(LineEntity.builder()
                .id("testId3")
                .country("FR")
                .otherCountry("GE")
                .build());

        Map<String, LineGeoData> lines = lineCustomRepository.getLines();

        assertEquals(3, lines.size());

        assertEquals("testId3", new ArrayList<>(lines.values()).get(0).getId());
        assertEquals("GE", new ArrayList<>(lines.values()).get(0).getCountry1().toString());
        assertEquals("FR", new ArrayList<>(lines.values()).get(0).getCountry2().toString());

        assertEquals("testId", new ArrayList<>(lines.values()).get(2).getId());
        assertEquals("BE", new ArrayList<>(lines.values()).get(2).getCountry1().toString());
        assertEquals("FR", new ArrayList<>(lines.values()).get(2).getCountry2().toString());
    }
}
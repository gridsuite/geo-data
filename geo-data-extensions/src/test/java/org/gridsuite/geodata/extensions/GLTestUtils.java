/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.geodata.extensions;

import com.powsybl.iidm.network.*;
import org.joda.time.DateTime;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.eu>
 */
public final class GLTestUtils {

    public static final Coordinate SUBSTATION_1 =  new Coordinate(51.380348205566406, 0.5492960214614868);
    public static final Coordinate SUBSTATION_2 = new Coordinate(52.00010299682617, 0.30759671330451965);
    public static final Coordinate LINE_1 = new Coordinate(51.529258728027344, 0.5132722854614258);
    public static final Coordinate LINE_2 = new Coordinate(51.944923400878906, 0.4120868146419525);

    private GLTestUtils() {
    }

    public static Network getNetwork() {
        Network network = NetworkFactory.create("Network", "test");
        network.setCaseDate(DateTime.parse("2018-01-01T00:30:00.000+01:00"));
        Substation substation1 = network.newSubstation()
                .setId("Substation1")
                .setCountry(Country.FR)
                .add();
        VoltageLevel voltageLevel1 = substation1.newVoltageLevel()
                .setId("VoltageLevel1")
                .setNominalV(400)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        voltageLevel1.getBusBreakerView().newBus()
                .setId("Bus1")
                .add();
        Substation substation2 = network.newSubstation()
                .setId("Substation2")
                .setCountry(Country.FR)
                .add();
        VoltageLevel voltageLevel2 = substation2.newVoltageLevel()
                .setId("VoltageLevel2")
                .setNominalV(400)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        voltageLevel2.getBusBreakerView().newBus()
                .setId("Bus2")
                .add();
        network.newLine()
                .setId("Line")
                .setVoltageLevel1(voltageLevel1.getId())
                .setBus1("Bus1")
                .setConnectableBus1("Bus1")
                .setVoltageLevel2(voltageLevel2.getId())
                .setBus2("Bus2")
                .setConnectableBus2("Bus2")
                .setR(3.0)
                .setX(33.0)
                .setG1(0.0)
                .setB1(386E-6 / 2)
                .setG2(0.0)
                .setB2(386E-6 / 2)
                .add();
        return network;
    }

    public static void checkNetwork(Network network) {
        Substation substation1 = network.getSubstation("Substation1");
        SubstationPosition substation1Position = substation1.getExtension(SubstationPosition.class);
        assertEquals(SUBSTATION_1.getLat(), substation1Position.getCoordinate().getLat(), 0);
        assertEquals(SUBSTATION_1.getLon(), substation1Position.getCoordinate().getLon(), 0);

        Substation substation2 = network.getSubstation("Substation2");
        SubstationPosition substation2Position = substation2.getExtension(SubstationPosition.class);
        assertEquals(SUBSTATION_2.getLat(), substation2Position.getCoordinate().getLat(), 0);
        assertEquals(SUBSTATION_2.getLon(), substation2Position.getCoordinate().getLon(), 0);

        Line line = network.getLine("Line");
        LinePosition<Line> linePosition = line.getExtension(LinePosition.class);
        assertEquals(SUBSTATION_1.getLat(), linePosition.getCoordinates().get(0).getLat(), 0);
        assertEquals(SUBSTATION_1.getLon(), linePosition.getCoordinates().get(0).getLon(), 0);

        assertEquals(LINE_1.getLat(), linePosition.getCoordinates().get(1).getLat(), 0);
        assertEquals(LINE_1.getLon(), linePosition.getCoordinates().get(1).getLon(), 0);

        assertEquals(LINE_2.getLat(), linePosition.getCoordinates().get(2).getLat(), 0);
        assertEquals(LINE_2.getLon(), linePosition.getCoordinates().get(2).getLon(), 0);

        assertEquals(SUBSTATION_2.getLat(), linePosition.getCoordinates().get(3).getLat(), 0);
        assertEquals(SUBSTATION_2.getLon(), linePosition.getCoordinates().get(3).getLon(), 0);
    }
}
/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.geodata.extensions;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.AbstractExtensionXmlSerializer;
import com.powsybl.commons.extensions.ExtensionXmlSerializer;
import com.powsybl.commons.xml.XmlReaderContext;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.commons.xml.XmlWriterContext;
import com.powsybl.iidm.network.DanglingLine;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Line;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.eu>
 */
@AutoService(ExtensionXmlSerializer.class)
public class LinePositionXmlSerializer<T extends Identifiable<T>> extends AbstractExtensionXmlSerializer<T, LinePosition<T>> {

    public LinePositionXmlSerializer() {
        super(LinePosition.NAME, "network", LinePosition.class, true, "linePosition.xsd",
                "http://www.itesla_project.eu/schema/iidm/ext/line_position/1_0", "lp");
    }

    @Override
    public void write(LinePosition<T> linePosition, XmlWriterContext context) throws XMLStreamException {
        for (Coordinate point : linePosition.getCoordinates()) {
            context.getWriter().writeEmptyElement(getNamespaceUri(), "coordinate");
            XmlUtil.writeDouble("longitude", point.getLon(), context.getWriter());
            XmlUtil.writeDouble("latitude", point.getLat(), context.getWriter());
        }
    }

    @Override
    public LinePosition<T> read(T line, XmlReaderContext context) throws XMLStreamException {
        List<Coordinate> coordinates = new ArrayList<>();
        XmlUtil.readUntilEndElement(getExtensionName(), context.getReader(), () -> {
            double longitude = XmlUtil.readDoubleAttribute(context.getReader(), "longitude");
            double latitude = XmlUtil.readDoubleAttribute(context.getReader(), "latitude");
            coordinates.add(new Coordinate(latitude, longitude));
        });
        return createLinePosition(line, coordinates);
    }

    private LinePosition<T> createLinePosition(T line, List<Coordinate> coordinates) {
        if (line instanceof Line) {
            return new LinePosition<>((Line) line, coordinates);
        } else if (line instanceof DanglingLine) {
            return new LinePosition<>((DanglingLine) line, coordinates);
        } else {
            throw new AssertionError("Unsupported equipment");
        }
    }

}

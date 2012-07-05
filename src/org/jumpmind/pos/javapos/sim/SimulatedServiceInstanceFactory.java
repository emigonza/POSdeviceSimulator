package org.jumpmind.pos.javapos.sim;

import java.lang.reflect.Constructor;

import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;

public class SimulatedServiceInstanceFactory implements
        JposServiceInstanceFactory {

    //@SuppressWarnings("unchecked")
    public JposServiceInstance createInstance(String s, JposEntry jposentry)
            throws JposException {
        if (!jposentry.hasPropertyWithName("serviceClass")) {
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'serviceClass' property");
        }

        try {
            String s1 = (String) jposentry.getPropertyValue("serviceClass");
            Class<?> class1 = Class.forName(s1);
            Constructor<?> constructor = class1.getConstructor(new Class[0]);
            return (JposServiceInstance) constructor.newInstance(new Object[0]);
        } catch (Exception exception) {
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "Could not create the service instance!", exception);
        }
    }

}

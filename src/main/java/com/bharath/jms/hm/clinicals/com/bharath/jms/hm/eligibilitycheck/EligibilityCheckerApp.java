package com.bharath.jms.hm.clinicals.com.bharath.jms.hm.eligibilitycheck;


import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EligibilityCheckerApp {
    public static void main(String[] args) throws NamingException, InterruptedException {
        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/requestQueue");
        try (
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
                JMSContext jmsContext = connectionFactory.createContext())
        {
            //Consumer
            JMSConsumer consumer = jmsContext.createConsumer(queue);
            consumer.setMessageListener(new EligibilityCheckListener());

            Thread.sleep(10000);

        }
    }
}

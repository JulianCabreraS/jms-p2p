package com.bharath.jms.hm.clinicals;

import com.bharath.jms.hm.model.Patient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class ClinicalsApp {
    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
                JMSContext jmsContext = connectionFactory.createContext())
        {
            //Producer
            JMSProducer producer = jmsContext.createProducer();
            ObjectMessage objectMessage = jmsContext.createObjectMessage();
            Patient patient = new Patient(123,"Bob", "Blue Cross Blue Shield", 30d, 500d);
            objectMessage.setObject(patient);

            for(int i=1; i<=10;i++)
            {
                producer.send(requestQueue, objectMessage);
            }


            //Consumer
            JMSConsumer consumer = jmsContext.createConsumer(replyQueue);
            MapMessage replyMessage = (MapMessage) consumer.receive(30000);
            System.out.println("Patient eligbility is: "+ replyMessage.getBoolean("eligible"));

        }
    }
}

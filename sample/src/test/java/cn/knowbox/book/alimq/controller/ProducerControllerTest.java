package cn.knowbox.book.alimq.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cn.knowbox.book.alimq.RocketMqSpringBootApplication;

/**
 * 发送测试
 *
 * @author Created by gold on 2019/10/5 15:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RocketMqSpringBootApplication.class})
@AutoConfigureMockMvc
public class ProducerControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    public void sendMqTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/mq/send/testMq"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sendAsyncMqTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/mq/sendAsync/testAsyncMq"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sendOrderMqTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/mq/sendOrder/testOrderMq"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sendTransactionMqTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/mq/sendTransaction/testTransactionMq"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}
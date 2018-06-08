package net.koffeepot.presetqueries.controller;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.given;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import net.koffeepot.presetqueries.service.RoleService;


@RunWith(SpringRunner.class)
//In order not to launch the whole stack, hibernate for instance, replace these annotations:
//@SpringBootTest
//@AutoConfigureMockMvc
//by this one:
@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RoleService roleService;

    @Test
    public void log_in() throws Exception {
        given(roleService.getAuthToken("alogin", "apwd")).willReturn("c-zerzerz");
        mvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"login\":\"alogin\", \"password\":\"apwd\"}")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("c-zerzerz"));
    }

    @Test
    public void log_in_with_no_body() throws Exception {
        mvc.perform(post("/api/login")).andExpect(status().isBadRequest());
    }


}



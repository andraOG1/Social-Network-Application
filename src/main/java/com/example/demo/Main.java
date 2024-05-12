package com.example.demo;


import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.service.UtilizatorService;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        //UserRepoDB u_repo = new UserRepoDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123");
        // System.out.println(u_repo.findOne(1l));
        // System.out.println(u_repo.findAll());
        UserRepoDB userRepoDB = new UserRepoDB("jdbc:postgresql://localhost:5434/postgres","postgres","postgres");
        UtilizatorService utilizatorService = new UtilizatorService(userRepoDB);

       List<Long> list =  utilizatorService.get_friends(2l);
       for(Long v   : list)
       {
           System.out.println(v);
       }




    }

}
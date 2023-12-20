package com.example.todo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthCheckContorller {
    
    /*
        - 왜 이런 클래스를 만들어서 사용하는지
        나중에 로드 밸런서라는 것이 있음.
        aws에서 제공하는 기능
        EC2 컴터가 장애가 발생할 것을 대비해서 여러개 서버로 나눔
        평소에 사용하던 서버가 장애가 나면 바로 만들어 놨던 다른 서버와 연결해주면서
        원래 사용하던 서버의 장애가 해결될 때까지 사용
        장애 해결 -> 원래 사용하던 서버사용 + 대비용 서버는 다시 비활성화 시킴
        이런 사실들을 로드 밸런서에게 알려줌
        위의 과정을 '로드 밸런싱' 이라함!

       !정리
       - 로드 밸런서를 이용할 때, 서버의 현재 상태를 확인하기 위한 메서드를 작성하여
       응답이 제대로 리턴이 되는 지를 확인하기 위한 컨트롤러와 메서드.
       - 응답이 제대로 전달되지 않는다면 서버에 장애가 발생했다고 가정하여
       로드 밸런서가 사본 인스턴스로 요청을 전환하여 계속해서 서비스가 제동 되도록 유도함!
     */

    @GetMapping("/") // 사용하던 매핑중에 "/"를 이미 사용하고 있다면 사용불가임
    public ResponseEntity<?> healthCheck(){

        log.info("server is running . . . I'm Healthy!");
        log.info("Hello World!");
        return ResponseEntity.ok().body("It's OK!");
    }


}

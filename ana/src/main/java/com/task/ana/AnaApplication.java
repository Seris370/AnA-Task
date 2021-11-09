package com.task.ana;

import com.task.ana.component.AnaManager;
import com.task.ana.model.Access;
import com.task.ana.model.Item;
import com.task.ana.component.UserRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AnaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnaApplication.class, args);
    }

    private String token = null;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World!";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam(name="username") String username,
                                      @RequestParam(name="password") String password,
                                      @RequestParam(name="access", defaultValue = "world") String access) {
        String hash = DigestUtils.md5DigestAsHex(password.getBytes());
        if (AnaManager.register(username, hash, access)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam(name="username") String username, @RequestParam(name="password") String password) {
        String hash = DigestUtils.md5DigestAsHex(password.getBytes());
        String token = AnaManager.login(username, hash);
        System.out.println(token);
        if (token != null) {
            this.token = token;
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Wrong username or password", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/items")
    public ResponseEntity<?> getText() {
        System.out.println(this.token);
        if (this.token == null || !AnaManager.authenticate(this.token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(Item.getSerializedItems(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestParam(name="text") String text) {
        System.out.println(this.token);
        if (this.token == null || !AnaManager.authenticate(this.token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (!AnaManager.getAccess(this.token).equals(Access.ADMIN)) {
            return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN);
        }
        int id = Item.addItem(text);
        return new ResponseEntity<>("Item added: " + id, HttpStatus.OK);
    }

    @PutMapping("/replace")
    public ResponseEntity<?> replaceItem(@RequestParam(name="id") int id, @RequestParam(name="text") String text) {
        System.out.println(this.token);
        if (this.token == null || !AnaManager.authenticate(this.token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (!AnaManager.getAccess(this.token).equals(Access.ADMIN)) {
            return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN);
        }
        boolean res = Item.replaceItem(id, text);
        return res ? new ResponseEntity<>("Item replaced", HttpStatus.OK)
                : new ResponseEntity<>("Fail to replace item", HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEmployee(@RequestParam(name="id") int id) {
        System.out.println(this.token);
        if (this.token == null || !AnaManager.authenticate(this.token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (!AnaManager.getAccess(this.token).equals(Access.ADMIN)) {
            return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN);
        }
        boolean res = Item.deleteItem(id);
        return res ? new ResponseEntity<>("Item deleted", HttpStatus.OK)
                : new ResponseEntity<>("Fail to delete item", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        AnaManager.logout(this.token);
        this.token = null;
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.model.enums.Role;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @Enumerated(EnumType.STRING)
    private Role role;
    @CreationTimestamp
    private LocalDateTime created;
    private boolean reported;
    @ManyToMany
    @JoinTable(name = "comments_reporters",
    joinColumns = {@JoinColumn(name = "comment_id")},
    inverseJoinColumns = {@JoinColumn(name = "reporter")})
    private Set<User> reportedBy;

    public Comment(String text, Event event, User author, Role role) {
        this.text = text;
        this.event = event;
        this.author = author;
        this.role = role;
    }
}
package com.vladmihalcea.hpjp.hibernate.mapping.enums;

import com.vladmihalcea.hpjp.util.AbstractPostgreSQLIntegrationTest;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Vlad Mihalcea
 */
public class EnumPostgreSQLTest extends AbstractPostgreSQLIntegrationTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
            Post.class,
        };
    }

    @Override
    protected void beforeInit() {
        executeStatement("DROP TABLE IF EXISTS post_status_info CASCADE");
        executeStatement("DROP TYPE post_status_info CASCADE");
        executeStatement("CREATE TYPE post_status_info AS ENUM ('PENDING', 'APPROVED', 'SPAM')");
    }

    @Override
    protected void afterDestroy() {
        executeStatement("DROP TYPE IF EXISTS post_status_info CASCADE");
    }

    @Test
    public void test() {
        doInJPA(entityManager -> {
            Post post = new Post();
            post.setId(1L);
            post.setTitle("High-Performance Java Persistence");
            post.setStatus(PostStatus.PENDING);
            entityManager.persist(post);
        });

        doInJPA(entityManager -> {
            Post post = entityManager.find(Post.class, 1L);
            assertEquals(PostStatus.PENDING, post.getStatus());
        });
    }

    public enum PostStatus {
        PENDING,
        APPROVED,
        SPAM
    }

    @Entity(name = "Post")
    @Table(name = "post")
    public static class Post {

        @Id
        private Long id;

        private String title;

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "post_status_info")
        @JdbcType(PostgreSQLEnumJdbcType.class)
        private PostStatus status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public PostStatus getStatus() {
            return status;
        }

        public void setStatus(PostStatus status) {
            this.status = status;
        }
    }
}

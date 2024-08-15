package com.next.genshinflow.domain.posting;

import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.LocalDateTimeAttributeConverter;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import com.next.genshinflow.enumeration.converter.QuestCategoryConverter;
import com.next.genshinflow.enumeration.converter.RegionConverter;
import com.next.genshinflow.util.HashedPassword;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "posting")
@Comment("메인 대시보드 posting")
public class Posting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private MemberEntity writer;

// TODO: FE 단에서 uid 만 내려줄 때 이름/이미지 가져올 수 있는지 문의할 것
//       가능할 경우, AssignerResponse / mapper 수정할 것
//
//    @Column(length = 20)
//    @Comment("비회원일 경우 비회원의 이름")
//    private String name;

    @Column
    private Long uid;

    @Convert(converter = RegionConverter.class)
    @Column
    private Region region;

    @Convert(converter = QuestCategoryConverter.class)
    @Column
    private QuestCategory questCategory;

    @Column
    private int worldLevel;

    @Column
    private String content;

    @Column
    private String password;

    @Column
    private String passwordSalt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean deleted;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column
    private LocalDateTime completedAt;

    public HashedPassword getHashedPassword() {
        return new HashedPassword(
            password,
            passwordSalt
        );
    }
}

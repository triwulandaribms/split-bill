// package com.allobankdev.splitbill.entity;


// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.NoArgsConstructor;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// import org.springframework.data.annotation.CreatedDate;
// import org.springframework.data.annotation.LastModifiedDate;

// @Entity
// @Table(
//     name = "expense_participants",
//     uniqueConstraints = {
//         @UniqueConstraint(
//             name = "uk_expense_participant",
//             columnNames = {"expense_id", "participant_id"}
//         )
//     }
// )
// @Getter
// @NoArgsConstructor
// public class ExpenseParticipant {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     @ManyToOne(fetch = FetchType.LAZY, optional = false)
//     @JoinColumn(name = "expense_id", nullable = false)
//     private Expense expense;

//     @ManyToOne(fetch = FetchType.LAZY, optional = false)
//     @JoinColumn(name = "participant_id", nullable = false)
//     private Participant participant;

//     @Column(name = "share_amount", nullable = false, precision = 19, scale = 2)
//     private BigDecimal shareAmount = BigDecimal.ZERO;

//     @CreatedDate
//     @Column(name = "created_at", nullable = false, updatable = false)
//     private LocalDateTime createdAt;

//     @LastModifiedDate
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;
    
//     @Column(name = "deleted_at")
//     private LocalDateTime deletedAt;

//     public ExpenseParticipant(
//             Expense expense,
//             Participant participant,
//             BigDecimal shareAmount) {

//         this.expense = expense;
//         this.participant = participant;
//         this.shareAmount = shareAmount;
//     }

//     public void update(BigDecimal shareAmount) {
//         this.shareAmount = shareAmount;
//     }
// }


package com.allobankdev.splitbill.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "expense_participants",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_expense_participant",
            columnNames = {"expense_id", "participant_id"}
        )
    }
)
@Getter
@NoArgsConstructor
public class ExpenseParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @Column(
        name = "share_amount",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal shareAmount = BigDecimal.ZERO;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public ExpenseParticipant(
            Expense expense,
            Participant participant,
            BigDecimal shareAmount) {

        this.expense = expense;
        this.participant = participant;
        this.shareAmount = shareAmount;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }
}
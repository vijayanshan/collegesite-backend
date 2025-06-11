package com.filmhook.college;

import jakarta.persistence.*;

@Entity
@Table(name = "college")

public class College {		

			    @Id
			    @GeneratedValue(strategy = GenerationType.IDENTITY) 
			    private Long id;

			    @Column(name = "name")
			    private String name;

			    public String getPhno() {
					return phno;
				}

				public void setPhno(String phno) {
					this.phno = phno;
				}

				public void setId(Long id) {
					this.id = id;
				}

				@Column(name = "email")
			    private String email;

			    
			    @Column(name = "phone_number")
			    private String phno;
			    

			    // Getters and Setters
			    public Long getId() {
			        return id;
			    }

			    public String getName() {
			        return name;
			    }

			    public void setName(String name) {
			        this.name = name;
			    }

			    public String getEmail() {
			        return email;
			    }

			    public void setEmail(String email) {
			        this.email = email;
			        
			    }
			   
			}

	


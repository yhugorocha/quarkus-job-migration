package com.extreme.app.dto;

import com.extreme.app.entities.AddressEntity;
import com.extreme.app.entities.AssistedEntity;
import com.extreme.app.entities.ContactEntity;
import com.extreme.app.entities.IdentityCardEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssistedInterior {
    private Long id;
    private String nome;
    private String nomeSocial;
    private String cpf;
    private String rg;
    private String pai;
    private String mae;
    private LocalDate dataNascimento;
    private String nacionalidade;
    private String naturalidade;
    private String cns;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cidade;
    private String sigla;
    private String bairro;
    private String telefone;
    private String celular;
    private String email;
    private Integer rn;

    public AssistedEntity toEntity(){
        return AssistedEntity.builder()
                .migratedInteriorId(this.id)
                .name(this.nome)
                .socialName(this.nomeSocial)
                .cpf(this.cpf)
                .identityCard(IdentityCardEntity.builder()
                        .number(this.rg)
                        .build())
                .fatherName(this.pai)
                .motherName(this.mae)
                .dateOfBirth(this.dataNascimento)
                .nationality(this.nacionalidade)
                .birthplace(this.naturalidade)
                .cns(this.cns)
                .address(AddressEntity.builder()
                        .postalCode(this.cep)
                        .city(this.cidade)
                        .fu(this.sigla)
                        .street(this.logradouro)
                        .neighborhood(this.bairro)
                        .number(this.numero)
                        .complement(this.complemento)
                        .build())
                .contact(ContactEntity.builder()
                        .homePhone(this.telefone)
                        .mobilePhone(this.celular)
                        .email(this.email)
                        .build())
                .build();
    }
}

package com.example.dedikodu_mesajlasmauygulamasi

class User {
    var uid:String?=null
    var name :String?=null
    var email:String?=null

    var image:String?=null
    var status:String?=null
    constructor(uid:String?,name:String?,email:String?,image:String?,status:String?){
        this.uid=uid
        this.name=name
        this.email=email

        this.image=image
        this.status=status

    }
}
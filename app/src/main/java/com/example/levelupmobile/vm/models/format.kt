package com.example.levelupmobile.vm.models
fun Long.toCLP(): String = "\$" + "%,d".format(this).replace(',', '.') + " CLP"

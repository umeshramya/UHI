class CancelAppointmentResponse {
  String? uuid;
  String? display;
  TimeSlot? timeSlot;
  dynamic visit;
  Patient? patient;
  String? status;
  String? reason;
  String? cancelReason;
  AppointmentType? appointmentType;
  bool? voided;
  List<Links>? links;
  String? resourceVersion;

  CancelAppointmentResponse(
      {this.uuid,
        this.display,
        this.timeSlot,
        this.visit,
        this.patient,
        this.status,
        this.reason,
        this.cancelReason,
        this.appointmentType,
        this.voided,
        this.links,
        this.resourceVersion});

  CancelAppointmentResponse.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    timeSlot = json['timeSlot'] != null
        ? TimeSlot.fromJson(json['timeSlot'])
        : null;
    visit = json['visit'];
    patient =
    json['patient'] != null ? Patient.fromJson(json['patient']) : null;
    status = json['status'];
    reason = json['reason'];
    cancelReason = json['cancelReason'];
    appointmentType = json['appointmentType'] != null
        ? AppointmentType.fromJson(json['appointmentType'])
        : null;
    voided = json['voided'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
    resourceVersion = json['resourceVersion'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (timeSlot != null) {
      data['timeSlot'] = timeSlot!.toJson();
    }
    data['visit'] = visit;
    if (patient != null) {
      data['patient'] = patient!.toJson();
    }
    data['status'] = status;
    data['reason'] = reason;
    data['cancelReason'] = cancelReason;
    if (appointmentType != null) {
      data['appointmentType'] = appointmentType!.toJson();
    }
    data['voided'] = voided;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    data['resourceVersion'] = resourceVersion;
    return data;
  }
}

class TimeSlot {
  String? uuid;
  String? display;
  String? startDate;
  String? endDate;
  AppointmentBlock? appointmentBlock;
  int? countOfAppointments;
  int? unallocatedMinutes;
  bool? voided;
  List<Links>? links;
  String? resourceVersion;

  TimeSlot(
      {this.uuid,
        this.display,
        this.startDate,
        this.endDate,
        this.appointmentBlock,
        this.countOfAppointments,
        this.unallocatedMinutes,
        this.voided,
        this.links,
        this.resourceVersion});

  TimeSlot.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    appointmentBlock = json['appointmentBlock'] != null
        ? AppointmentBlock.fromJson(json['appointmentBlock'])
        : null;
    countOfAppointments = json['countOfAppointments'];
    unallocatedMinutes = json['unallocatedMinutes'];
    voided = json['voided'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
    resourceVersion = json['resourceVersion'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    data['startDate'] = startDate;
    data['endDate'] = endDate;
    if (appointmentBlock != null) {
      data['appointmentBlock'] = appointmentBlock!.toJson();
    }
    data['countOfAppointments'] = countOfAppointments;
    data['unallocatedMinutes'] = unallocatedMinutes;
    data['voided'] = voided;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    data['resourceVersion'] = resourceVersion;
    return data;
  }
}

class AppointmentType {
  String? uuid;
  String? display;
  List<Links>? links;

  AppointmentType({this.uuid, this.display, this.links});

  AppointmentType.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'].toString();
    display = json['display'].toString();
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Location {
  String? uuid;
  String? display;
  List<Links>? links;

  Location({this.uuid, this.display, this.links});

  Location.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'].toString();
    display = json['display'].toString();
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class AppointmentBlock {
  String? uuid;
  String? display;
  String? startDate;
  String? endDate;
  Provider? provider;
  Location? location;
  List<AppointmentType>? types;
  bool? voided;
  List<Links>? links;
  String? resourceVersion;

  AppointmentBlock(
      {this.uuid,
        this.display,
        this.startDate,
        this.endDate,
        this.provider,
        this.location,
        this.types,
        this.voided,
        this.links,
        this.resourceVersion});

  AppointmentBlock.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    provider = json['provider'] != null
        ? Provider.fromJson(json['provider'])
        : null;
    location =
    json['location'] != null ? Location.fromJson(json['location']) : null;
    if (json['types'] != null) {
      types = <AppointmentType>[];
      json['types'].forEach((v) {
        types!.add(AppointmentType.fromJson(v));
      });
    }
    voided = json['voided'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
    resourceVersion = json['resourceVersion'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    data['startDate'] = startDate;
    data['endDate'] = endDate;
    if (provider != null) {
      data['provider'] = provider!.toJson();
    }
    if (location != null) {
      data['location'] = location!.toJson();
    }
    if (types != null) {
      data['types'] = types!.map((v) => v.toJson()).toList();
    }
    data['voided'] = voided;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    data['resourceVersion'] = resourceVersion;
    return data;
  }
}

class ProviderPerson {
  String? uuid;
  String? display;
  List<Links>? links;

  ProviderPerson({this.uuid, this.display, this.links});

  ProviderPerson.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'].toString();
    display = json['display'].toString();
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Provider {
  String? uuid;
  String? display;
  ProviderPerson? person;
  String? identifier;
  List<ProviderAttributes>? attributes;
  bool? retired;
  List<Links>? links;
  String? resourceVersion;

  Provider(
      {this.uuid,
        this.display,
        this.person,
        this.identifier,
        this.attributes,
        this.retired,
        this.links,
        this.resourceVersion});

  Provider.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    person =
    json['person'] != null ? ProviderPerson.fromJson(json['person']) : null;
    identifier = json['identifier'];
    if (json['attributes'] != null) {
      attributes = <ProviderAttributes>[];
      json['attributes'].forEach((v) {
        attributes!.add(ProviderAttributes.fromJson(v));
      });
    }
    retired = json['retired'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
    resourceVersion = json['resourceVersion'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (person != null) {
      data['person'] = person!.toJson();
    }
    data['identifier'] = identifier;
    if (attributes != null) {
      data['attributes'] = attributes!.map((v) => v.toJson()).toList();
    }
    data['retired'] = retired;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    data['resourceVersion'] = resourceVersion;
    return data;
  }
}

class ProviderAttributes{
  String? uuid;
  String? display;
  List<Links>? links;

  ProviderAttributes({this.uuid, this.display, this.links});

  ProviderAttributes.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

/*class Person {
  String? uuid;
  String? display;
  List<Links>? links;

  Person({this.uuid, this.display, this.links});

  Person.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}*/

class Links {
  String? rel;
  String? uri;
  String? resourceAlias;

  Links({this.rel, this.uri, this.resourceAlias});

  Links.fromJson(Map<String, dynamic> json) {
    rel = json['rel'];
    uri = json['uri'];
    resourceAlias = json['resourceAlias'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['rel'] = rel;
    data['uri'] = uri;
    data['resourceAlias'] = resourceAlias;
    return data;
  }
}

class PatientIdentifiers{
  String? uuid;
  String? display;
  List<Links>? links;

  PatientIdentifiers({this.uuid, this.display, this.links});

  PatientIdentifiers.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Patient {
  String? uuid;
  String? display;
  List<PatientIdentifiers>? identifiers;
  Person? person;
  bool? voided;
  List<Links>? links;
  String? resourceVersion;

  Patient(
      {this.uuid,
        this.display,
        this.identifiers,
        this.person,
        this.voided,
        this.links,
        this.resourceVersion});

  Patient.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    if (json['identifiers'] != null) {
      identifiers = <PatientIdentifiers>[];
      json['identifiers'].forEach((v) {
        identifiers!.add(PatientIdentifiers.fromJson(v));
      });
    }
    person =
    json['person'] != null ? Person.fromJson(json['person']) : null;
    voided = json['voided'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
    resourceVersion = json['resourceVersion'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    if (identifiers != null) {
      data['identifiers'] = identifiers!.map((v) => v.toJson()).toList();
    }
    if (person != null) {
      data['person'] = person!.toJson();
    }
    data['voided'] = voided;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    data['resourceVersion'] = resourceVersion;
    return data;
  }
}

class Person {
  String? uuid;
  String? display;
  String? gender;
  int? age;
  String? birthdate;
  bool? birthdateEstimated;
  bool? dead;
  String? deathDate;
  String? causeOfDeath;
  ProviderPerson? preferredName;
  ProviderPerson? preferredAddress;
  //List<Null>? attributes;
  bool? voided;
  String? birthtime;
  bool? deathdateEstimated;
  List<Links>? links;
  String? resourceVersion;

  Person(
      {this.uuid,
        this.display,
        this.gender,
        this.age,
        this.birthdate,
        this.birthdateEstimated,
        this.dead,
        this.deathDate,
        this.causeOfDeath,
        this.preferredName,
        this.preferredAddress,
        //this.attributes,
        this.voided,
        this.birthtime,
        this.deathdateEstimated,
        this.links,
        this.resourceVersion});

  Person.fromJson(Map<String, dynamic> json) {
    uuid = json['uuid'];
    display = json['display'];
    gender = json['gender'];
    age = json['age'];
    birthdate = json['birthdate'];
    birthdateEstimated = json['birthdateEstimated'];
    dead = json['dead'];
    deathDate = json['deathDate'];
    causeOfDeath = json['causeOfDeath'];
    preferredName = json['preferredName'] != null
        ? ProviderPerson.fromJson(json['preferredName'])
        : null;
    preferredAddress = json['preferredAddress'] != null
        ? ProviderPerson.fromJson(json['preferredAddress'])
        : null;
   /* if (json['attributes'] != null) {
      attributes = <Null>[];
      json['attributes'].forEach((v) {
        attributes!.add(new Null.fromJson(v));
      });
    }*/
    voided = json['voided'];
    birthtime = json['birthtime'];
    deathdateEstimated = json['deathdateEstimated'];
    if (json['links'] != null) {
      links = <Links>[];
      json['links'].forEach((v) {
        links!.add(Links.fromJson(v));
      });
    }
    resourceVersion = json['resourceVersion'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['uuid'] = uuid;
    data['display'] = display;
    data['gender'] = gender;
    data['age'] = age;
    data['birthdate'] = birthdate;
    data['birthdateEstimated'] = birthdateEstimated;
    data['dead'] = dead;
    data['deathDate'] = deathDate;
    data['causeOfDeath'] = causeOfDeath;
    if (preferredName != null) {
      data['preferredName'] = preferredName!.toJson();
    }
    if (preferredAddress != null) {
      data['preferredAddress'] = preferredAddress!.toJson();
    }
    /*if (this.attributes != null) {
      data['attributes'] = this.attributes!.map((v) => v.toJson()).toList();
    }*/
    data['voided'] = voided;
    data['birthtime'] = birthtime;
    data['deathdateEstimated'] = deathdateEstimated;
    if (links != null) {
      data['links'] = links!.map((v) => v.toJson()).toList();
    }
    data['resourceVersion'] = resourceVersion;
    return data;
  }
}

<template>
    <div>
        <String
            label="Name"
            v-model="value.name"
            :editMode="editMode"
        />
        <String
            label="NameHanja"
            v-model="value.nameHanja"
            :editMode="editMode"
        />
        <String
            label="Rrn"
            v-model="value.rrn"
            :editMode="editMode"
        />
        <String
            label="Gender"
            v-model="value.gender"
            :editMode="editMode"
        />
        <String
            label="Religion"
            v-model="value.religion"
            :editMode="editMode"
        />
        <String
            label="RelationToHouseholdHead"
            v-model="value.relationToHouseholdHead"
            :editMode="editMode"
        />
        <Date
            label="ReportRegistrationDate"
            v-model="value.reportRegistrationDate"
            :editMode="editMode"
        />
        <String
            label="ReportUserId"
            v-model="value.reportUserId"
            :editMode="editMode"
        />
        <String
            label="ReporterName"
            v-model="value.reporterName"
            :editMode="editMode"
        />
        <String
            label="ReporterRrn"
            v-model="value.reporterRrn"
            :editMode="editMode"
        />
        <String
            label="ReporterRelationToDeceased"
            v-model="value.reporterRelationToDeceased"
            :editMode="editMode"
        />
        <String
            label="ReporterAddress"
            v-model="value.reporterAddress"
            :editMode="editMode"
        />
        <String
            label="ReporterPhone"
            v-model="value.reporterPhone"
            :editMode="editMode"
        />
        <String
            label="ReporterEmail"
            v-model="value.reporterEmail"
            :editMode="editMode"
        />
        <String
            label="SubmitterName"
            v-model="value.submitterName"
            :editMode="editMode"
        />
        <String
            label="SubmitterRrn"
            v-model="value.submitterRrn"
            :editMode="editMode"
        />
        <String
            label="FuneralCompanyName"
            v-model="value.funeralCompanyName"
            :editMode="editMode"
        />
        <String
            label="DirectorName"
            v-model="value.directorName"
            :editMode="editMode"
        />
        <String
            label="DirectorPhone"
            v-model="value.directorPhone"
            :editMode="editMode"
        />
        <String
            label="FuneralHomeName"
            v-model="value.funeralHomeName"
            :editMode="editMode"
        />
        <String
            label="MortuaryInfo"
            v-model="value.mortuaryInfo"
            :editMode="editMode"
        />
        <String
            label="FuneralHomeAddress"
            v-model="value.funeralHomeAddress"
            :editMode="editMode"
        />
        <String
            label="FuneralDuration"
            v-model="value.funeralDuration"
            :editMode="editMode"
        />
        <Date
            label="ProcessionDateTime"
            v-model="value.processionDateTime"
            :editMode="editMode"
        />
        <String
            label="BurialSiteInfo"
            v-model="value.burialSiteInfo"
            :editMode="editMode"
        />
        <v-divider class="border-opacity-50 my-divider my-2"></v-divider>
        <div variant="outlined" class="my-2">
            <String
                label="ChiefMourners"
                v-model="chiefMournersInput"
                :editMode="editMode"
                @keydown.enter="addChiefMournersList"
            />
            <v-card v-if="value.chiefMourners.length > 0" variant="outlined" class="pa-4 mt-2">
                <v-card-sub-title>
                    ChiefMourners List
                </v-card-sub-title>
                <li v-for="(name, index) in value.chiefMourners" :key="index">
                    {{ name }}
                </li>
            </v-card>
            <v-row class="ma-0 pa-0 mt-2">
                <v-spacer></v-spacer>
                <v-btn @click="addChiefMournersList">
                    ChiefMourners 추가
                </v-btn>
            </v-row>
            
            <StringDetailGrid
                label="ChiefMourners"
                offline
                v-model="value.chiefMourners"
                :editMode="editMode"
                @change="change"
            />
        </div>
        <v-divider class="border-opacity-50 my-divider my-2"></v-divider>
        <String
            label="TemplateKeyword"
            v-model="value.templateKeyword"
            :editMode="editMode"
        />
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn width="64px" color="primary" @click="save">
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>
import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'FuneralInfo',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "funeralInfos",
        chiefMournersInput: null,
        value: {
            chiefMourners: [],
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
        addChiefMournersList() {
            if (this.chiefMournersInput !== null && this.chiefMournersInput !== '') {
                this.value.chiefMourners.push(this.chiefMournersInput);
                this.chiefMournersInput = null; // null로 초기화
            }
        },
    },
}
</script>

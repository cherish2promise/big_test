<template>
    <div>
        <Number
            label="LoginId"
            v-model="value.loginId"
            :editMode="editMode"
        />
        <String
            label="Name"
            v-model="value.name"
            :editMode="editMode"
        />
        <String
            label="Rnn"
            v-model="value.rnn"
            :editMode="editMode"
        />
        <Number
            label="Age"
            v-model="value.age"
            :editMode="editMode"
        />
        <String
            label="Gender"
            v-model="value.gender"
            :editMode="editMode"
        />
        <String
            label="Address"
            v-model="value.address"
            :editMode="editMode"
        />
        <String
            label="Email"
            v-model="value.email"
            :editMode="editMode"
        />
        <String
            label="Phone"
            v-model="value.phone"
            :editMode="editMode"
        />
        <String
            label="Job"
            v-model="value.job"
            :editMode="editMode"
        />
        <Boolean
            label="HasChildren"
            v-model="value.hasChildren"
            :editMode="editMode"
        />
        <Boolean
            label="IsMarried"
            v-model="value.isMarried"
            :editMode="editMode"
        />
        <v-divider class="border-opacity-50 my-divider my-2"></v-divider>
        <div variant="outlined" class="my-2">
            <String
                label="DiseaseList"
                v-model="diseaseListInput"
                :editMode="editMode"
                @keydown.enter="addDiseaseListList"
            />
            <v-card v-if="value.diseaseList.length > 0" variant="outlined" class="pa-4 mt-2">
                <v-card-sub-title>
                    DiseaseList List
                </v-card-sub-title>
                <li v-for="(name, index) in value.diseaseList" :key="index">
                    {{ name }}
                </li>
            </v-card>
            <v-row class="ma-0 pa-0 mt-2">
                <v-spacer></v-spacer>
                <v-btn @click="addDiseaseListList">
                    DiseaseList 추가
                </v-btn>
            </v-row>
            
            <StringDetailGrid
                label="DiseaseList"
                offline
                v-model="value.diseaseList"
                :editMode="editMode"
                @change="change"
            />
        </div>
        <v-divider class="border-opacity-50 my-divider my-2"></v-divider>
        <Date
            label="BirthDate"
            v-model="value.birthDate"
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
    name: 'CustomerProfile',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "customerProfiles",
        diseaseListInput: null,
        value: {
            diseaseList: [],
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
        addDiseaseListList() {
            if (this.diseaseListInput !== null && this.diseaseListInput !== '') {
                this.value.diseaseList.push(this.diseaseListInput);
                this.diseaseListInput = null; // null로 초기화
            }
        },
    },
}
</script>

<template>
    <v-container>
        <v-snackbar
            v-model="snackbar.status"
            :timeout="snackbar.timeout"
            :color="snackbar.color"
        >
            
            <v-btn style="margin-left: 80px;" text @click="snackbar.status = false">
                Close
            </v-btn>
        </v-snackbar>
        <div class="panel">
            <div class="gs-bundle-of-buttons" style="max-height:10vh;">
                <v-btn @click="addNewRow" @class="contrast-primary-text" small color="primary">
                    <v-icon small style="margin-left: -5px;">mdi-plus</v-icon>등록
                </v-btn>
                <v-btn :disabled="!selectedRow" style="margin-left: 5px;" @click="openEditDialog()" class="contrast-primary-text" small color="primary">
                    <v-icon small>mdi-pencil</v-icon>수정
                </v-btn>
            </div>
            <div class="mb-5 text-lg font-bold"></div>
            <div class="table-responsive">
                <v-table>
                    <thead>
                        <tr>
                        <th>Id</th>
                        <th>LoginId</th>
                        <th>Name</th>
                        <th>Rnn</th>
                        <th>Age</th>
                        <th>Gender</th>
                        <th>Address</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Job</th>
                        <th>HasChildren</th>
                        <th>IsMarried</th>
                        <th>DiseaseList</th>
                        <th>BirthDate</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(val, idx) in value" 
                            @click="changeSelectedRow(val)"
                            :key="val"  
                            :style="val === selectedRow ? 'background-color: rgb(var(--v-theme-primary), 0.2) !important;':''"
                        >
                            <td class="font-semibold">{{ idx + 1 }}</td>
                            <td class="whitespace-nowrap" label="LoginId">{{ val.loginId }}</td>
                            <td class="whitespace-nowrap" label="Name">{{ val.name }}</td>
                            <td class="whitespace-nowrap" label="Rnn">{{ val.rnn }}</td>
                            <td class="whitespace-nowrap" label="Age">{{ val.age }}</td>
                            <td class="whitespace-nowrap" label="Gender">{{ val.gender }}</td>
                            <td class="whitespace-nowrap" label="Address">{{ val.address }}</td>
                            <td class="whitespace-nowrap" label="Email">{{ val.email }}</td>
                            <td class="whitespace-nowrap" label="Phone">{{ val.phone }}</td>
                            <td class="whitespace-nowrap" label="Job">{{ val.job }}</td>
                            <td class="whitespace-nowrap" label="HasChildren">{{ val.hasChildren }}</td>
                            <td class="whitespace-nowrap" label="IsMarried">{{ val.isMarried }}</td>
                            <td class="whitespace-nowrap" label="DiseaseList">
                                <span v-for="(name, index) in val.diseaseList" :key="index">
                                    {{ name }}<br>
                                </span>
                            </td>
                            <td class="whitespace-nowrap" label="BirthDate">{{ val.birthDate }}</td>
                            <v-row class="ma-0 pa-4 align-center">
                                <v-spacer></v-spacer>
                                <Icon style="cursor: pointer;" icon="mi:delete" @click="deleteRow(val)" />
                            </v-row>
                        </tr>
                    </tbody>
                </v-table>
            </div>
            <StringDetailGrid style="margin-top: 20px;" label="DiseaseList" offline v-if="selectedRow" v-model="selectedRow.diseaseList" :selectedRow="selectedRow"/>
        </div>
        <v-col>
            <v-dialog
                v-model="openDialog"
                transition="dialog-bottom-transition"
                width="35%"
            >
                <v-card>
                    <v-toolbar
                        color="primary"
                        class="elevation-0 pa-4"
                        height="50px"
                    >
                        <div style="color:white; font-size:17px; font-weight:700;">CustomerProfile 등록</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <CustomerProfile :offline="offline"
                            :isNew="!value.idx"
                            :editMode="true"
                            :inList="false"
                            v-model="newValue"
                            @add="append"
                        />
                    </v-card-text>
                </v-card>
            </v-dialog>
            <v-dialog
                v-model="editDialog"
                transition="dialog-bottom-transition"
                width="35%"
            >
                <v-card>
                    <v-toolbar
                        color="primary"
                        class="elevation-0 pa-4"
                        height="50px"
                    >
                        <div style="color:white; font-size:17px; font-weight:700;">CustomerProfile 수정</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <div>
                            <Number label="LoginId" v-model="selectedRow.loginId" :editMode="true"/>
                            <String label="Name" v-model="selectedRow.name" :editMode="true"/>
                            <String label="Rnn" v-model="selectedRow.rnn" :editMode="true"/>
                            <Number label="Age" v-model="selectedRow.age" :editMode="true"/>
                            <String label="Gender" v-model="selectedRow.gender" :editMode="true"/>
                            <String label="Address" v-model="selectedRow.address" :editMode="true"/>
                            <String label="Email" v-model="selectedRow.email" :editMode="true"/>
                            <String label="Phone" v-model="selectedRow.phone" :editMode="true"/>
                            <String label="Job" v-model="selectedRow.job" :editMode="true"/>
                            <Boolean label="HasChildren" v-model="selectedRow.hasChildren" :editMode="true"/>
                            <Boolean label="IsMarried" v-model="selectedRow.isMarried" :editMode="true"/>
                            < label="DiseaseList" v-model="selectedRow.diseaseList" :editMode="true"/>
                            <Date label="BirthDate" v-model="selectedRow.birthDate" :editMode="true"/>
                            <StringDetailGrid label="DiseaseList" offline v-model="selectedRow.diseaseList" :editMode="true"/>
                            <v-divider class="border-opacity-100 my-divider"></v-divider>
                            <v-layout row justify-end>
                                <v-btn
                                    width="64px"
                                    color="primary"
                                    @click="save"
                                >
                                    수정
                                </v-btn>
                            </v-layout>
                        </div>
                    </v-card-text>
                </v-card>
            </v-dialog>
        </v-col>
    </v-container>
</template>

<script>
import { ref } from 'vue';
import { useTheme } from 'vuetify';
import BaseGrid from '../base-ui/BaseGrid.vue'


export default {
    name: 'customerProfileGrid',
    mixins:[BaseGrid],
    components:{
    },
    data: () => ({
        path: 'customerProfiles',
    }),
    watch: {
    },
    methods:{
    }
}

</script>
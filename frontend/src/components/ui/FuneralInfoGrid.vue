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
                <v-btn :disabled="!selectedRow" style="margin-left: 5px;" @click="registerFuneralInfo" class="contrast-primary-text" small color="primary" :disabled="!hasRole('manager')">
                    <v-icon small>mdi-minus-circle-outline</v-icon>장례정보등록
                </v-btn>
            </div>
            <div class="mb-5 text-lg font-bold"></div>
            <div class="table-responsive">
                <v-table>
                    <thead>
                        <tr>
                        <th>Id</th>
                        <th>Name</th>
                        <th>NameHanja</th>
                        <th>Rrn</th>
                        <th>Gender</th>
                        <th>Religion</th>
                        <th>RelationToHouseholdHead</th>
                        <th>ReportRegistrationDate</th>
                        <th>ReportUserId</th>
                        <th>ReporterName</th>
                        <th>ReporterRrn</th>
                        <th>ReporterRelationToDeceased</th>
                        <th>ReporterAddress</th>
                        <th>ReporterPhone</th>
                        <th>ReporterEmail</th>
                        <th>SubmitterName</th>
                        <th>SubmitterRrn</th>
                        <th>FuneralCompanyName</th>
                        <th>DirectorName</th>
                        <th>DirectorPhone</th>
                        <th>FuneralHomeName</th>
                        <th>MortuaryInfo</th>
                        <th>FuneralHomeAddress</th>
                        <th>FuneralDuration</th>
                        <th>ProcessionDateTime</th>
                        <th>BurialSiteInfo</th>
                        <th>ChiefMourners</th>
                        <th>TemplateKeyword</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(val, idx) in value" 
                            @click="changeSelectedRow(val)"
                            :key="val"  
                            :style="val === selectedRow ? 'background-color: rgb(var(--v-theme-primary), 0.2) !important;':''"
                        >
                            <td class="font-semibold">{{ idx + 1 }}</td>
                            <td class="whitespace-nowrap" label="Name">{{ val.name }}</td>
                            <td class="whitespace-nowrap" label="NameHanja">{{ val.nameHanja }}</td>
                            <td class="whitespace-nowrap" label="Rrn">{{ val.rrn }}</td>
                            <td class="whitespace-nowrap" label="Gender">{{ val.gender }}</td>
                            <td class="whitespace-nowrap" label="Religion">{{ val.religion }}</td>
                            <td class="whitespace-nowrap" label="RelationToHouseholdHead">{{ val.relationToHouseholdHead }}</td>
                            <td class="whitespace-nowrap" label="ReportRegistrationDate">{{ val.reportRegistrationDate }}</td>
                            <td class="whitespace-nowrap" label="ReportUserId">{{ val.reportUserId }}</td>
                            <td class="whitespace-nowrap" label="ReporterName">{{ val.reporterName }}</td>
                            <td class="whitespace-nowrap" label="ReporterRrn">{{ val.reporterRrn }}</td>
                            <td class="whitespace-nowrap" label="ReporterRelationToDeceased">{{ val.reporterRelationToDeceased }}</td>
                            <td class="whitespace-nowrap" label="ReporterAddress">{{ val.reporterAddress }}</td>
                            <td class="whitespace-nowrap" label="ReporterPhone">{{ val.reporterPhone }}</td>
                            <td class="whitespace-nowrap" label="ReporterEmail">{{ val.reporterEmail }}</td>
                            <td class="whitespace-nowrap" label="SubmitterName">{{ val.submitterName }}</td>
                            <td class="whitespace-nowrap" label="SubmitterRrn">{{ val.submitterRrn }}</td>
                            <td class="whitespace-nowrap" label="FuneralCompanyName">{{ val.funeralCompanyName }}</td>
                            <td class="whitespace-nowrap" label="DirectorName">{{ val.directorName }}</td>
                            <td class="whitespace-nowrap" label="DirectorPhone">{{ val.directorPhone }}</td>
                            <td class="whitespace-nowrap" label="FuneralHomeName">{{ val.funeralHomeName }}</td>
                            <td class="whitespace-nowrap" label="MortuaryInfo">{{ val.mortuaryInfo }}</td>
                            <td class="whitespace-nowrap" label="FuneralHomeAddress">{{ val.funeralHomeAddress }}</td>
                            <td class="whitespace-nowrap" label="FuneralDuration">{{ val.funeralDuration }}</td>
                            <td class="whitespace-nowrap" label="ProcessionDateTime">{{ val.processionDateTime }}</td>
                            <td class="whitespace-nowrap" label="BurialSiteInfo">{{ val.burialSiteInfo }}</td>
                            <td class="whitespace-nowrap" label="ChiefMourners">
                                <span v-for="(name, index) in val.chiefMourners" :key="index">
                                    {{ name }}<br>
                                </span>
                            </td>
                            <td class="whitespace-nowrap" label="TemplateKeyword">{{ val.templateKeyword }}</td>
                            <v-row class="ma-0 pa-4 align-center">
                                <v-spacer></v-spacer>
                                <Icon style="cursor: pointer;" icon="mi:delete" @click="deleteRow(val)" />
                            </v-row>
                        </tr>
                    </tbody>
                </v-table>
            </div>
            <StringDetailGrid style="margin-top: 20px;" label="ChiefMourners" offline v-if="selectedRow" v-model="selectedRow.chiefMourners" :selectedRow="selectedRow"/>
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
                        <div style="color:white; font-size:17px; font-weight:700;">FuneralInfo 등록</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <FuneralInfo :offline="offline"
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
                        <div style="color:white; font-size:17px; font-weight:700;">FuneralInfo 수정</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <div>
                            <String label="Name" v-model="selectedRow.name" :editMode="true"/>
                            <String label="NameHanja" v-model="selectedRow.nameHanja" :editMode="true"/>
                            <String label="Rrn" v-model="selectedRow.rrn" :editMode="true"/>
                            <String label="Gender" v-model="selectedRow.gender" :editMode="true"/>
                            <String label="Religion" v-model="selectedRow.religion" :editMode="true"/>
                            <String label="RelationToHouseholdHead" v-model="selectedRow.relationToHouseholdHead" :editMode="true"/>
                            <Date label="ReportRegistrationDate" v-model="selectedRow.reportRegistrationDate" :editMode="true"/>
                            <String label="ReportUserId" v-model="selectedRow.reportUserId" :editMode="true"/>
                            <String label="ReporterName" v-model="selectedRow.reporterName" :editMode="true"/>
                            <String label="ReporterRrn" v-model="selectedRow.reporterRrn" :editMode="true"/>
                            <String label="ReporterRelationToDeceased" v-model="selectedRow.reporterRelationToDeceased" :editMode="true"/>
                            <String label="ReporterAddress" v-model="selectedRow.reporterAddress" :editMode="true"/>
                            <String label="ReporterPhone" v-model="selectedRow.reporterPhone" :editMode="true"/>
                            <String label="ReporterEmail" v-model="selectedRow.reporterEmail" :editMode="true"/>
                            <String label="SubmitterName" v-model="selectedRow.submitterName" :editMode="true"/>
                            <String label="SubmitterRrn" v-model="selectedRow.submitterRrn" :editMode="true"/>
                            <String label="FuneralCompanyName" v-model="selectedRow.funeralCompanyName" :editMode="true"/>
                            <String label="DirectorName" v-model="selectedRow.directorName" :editMode="true"/>
                            <String label="DirectorPhone" v-model="selectedRow.directorPhone" :editMode="true"/>
                            <String label="FuneralHomeName" v-model="selectedRow.funeralHomeName" :editMode="true"/>
                            <String label="MortuaryInfo" v-model="selectedRow.mortuaryInfo" :editMode="true"/>
                            <String label="FuneralHomeAddress" v-model="selectedRow.funeralHomeAddress" :editMode="true"/>
                            <String label="FuneralDuration" v-model="selectedRow.funeralDuration" :editMode="true"/>
                            <Date label="ProcessionDateTime" v-model="selectedRow.processionDateTime" :editMode="true"/>
                            <String label="BurialSiteInfo" v-model="selectedRow.burialSiteInfo" :editMode="true"/>
                            < label="ChiefMourners" v-model="selectedRow.chiefMourners" :editMode="true"/>
                            <String label="TemplateKeyword" v-model="selectedRow.templateKeyword" :editMode="true"/>
                            <StringDetailGrid label="ChiefMourners" offline v-model="selectedRow.chiefMourners" :editMode="true"/>
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
    name: 'funeralInfoGrid',
    mixins:[BaseGrid],
    components:{
    },
    data: () => ({
        path: 'funeralInfos',
    }),
    watch: {
    },
    methods:{
        async registerFuneralInfo(){
            try{
                var path = "registerFuneralInfo".toLowerCase();
                var temp = await this.repository.invoke(this.selectedRow, path, null)
                // 스넥바 관련 수정 필요
                // this.$EventBus.$emit('show-success','registerFuneralInfo 성공적으로 처리되었습니다.')
                for(var i = 0; i< this.value.length; i++){
                    if(this.value[i] == this.selectedRow){
                        this.value[i] = temp.data
                    }
                }
            }catch(e){
                console.log(e)
            }
        },
    }
}

</script>
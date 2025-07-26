<template>
    <div>
        <Number
            label="DocId"
            v-model="value.docId"
            :editMode="editMode"
        />
        <Number
            label="TemplateId"
            v-model="value.templateId"
            :editMode="editMode"
        />
        <String
            label="FileUrl"
            v-model="value.fileUrl"
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
    name: 'DocumentSave',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "documentSaves",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>

<script setup>
import {
  VerticalNavLink,
  VerticalNavSectionTitle,
} from '@layouts'
</script>

<template>
    <ul>
        <VerticalNavSectionTitle :item="{ heading: 'user-context' }" />
        <VerticalNavLink
            :item="{
                title: '사용자',
                to: '/users',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '고객프로필',
                to: '/customerProfiles',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'documentsCreated' }" />
        <VerticalNavLink
            :item="{
                title: '장례 정보',
                to: '/funeralInfos',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '문서저장',
                to: '/documentSaves',
            }"
        />
    </ul>
</template>

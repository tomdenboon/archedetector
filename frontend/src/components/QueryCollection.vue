<template>
  <div style="height: calc(100vh); width: calc(100vw); overflow: hidden">
    <search-navbar />
    <div class="d-flex">
      <query-collection-sidebar />
      <router-view :key='$route.params.id'></router-view>
    </div>
  </div>
</template>

<script>
import SearchNavbar from "@/components/navbar/SearchNavbar";
import QueryCollectionSidebar from "@/components/sidebar/QueryCollectionSidebar";

export default {
  name: 'MailingList',
  components:{
    SearchNavbar,
    QueryCollectionSidebar,
  },
}
</script>

<style>
</style>
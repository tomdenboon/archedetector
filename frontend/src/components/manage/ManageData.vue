<template>
    <div>
      <standard-navbar />
      <div class="d-flex">
        <manage-data-sidebar/>
        <router-view></router-view>
      </div>
    </div>
</template>

<script>
import StandardNavbar from "@/components/navbar/StandardNavbar";
import ManageDataSidebar from "@/components/sidebar/ManageDataSidebar";

export default {
  name: "ManageData",
  components: {
    StandardNavbar,
    ManageDataSidebar
  }
}
</script>

<style scoped>

</style>
<template>
  <div id="list-style" class="border-end" >
    <b-nav vertical>
      <b-nav-item :to="{ name: 'ManageQueryCollection' }"> Query Collection </b-nav-item>
      <b-nav-item :to="{ name: 'ManageIssueList' }">Issue List</b-nav-item>
      <b-nav-item :to="{ name: 'ManageMailingList' }">Mailing List</b-nav-item>
      <b-nav-item :to="{ name: 'ManageTag' }"> Tag </b-nav-item>
    </b-nav>
  </div>
</template>

<script>
export default {
  name: "ManageDataSidebar"
}

</script>

<style scoped>
#list-style{
  width: 300px;
  height: calc(100vh - 70px);
}
</style>
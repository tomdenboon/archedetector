<template>
  <div>
    <standard-navbar/>
    <div class="d-flex flex-column justify-content-center align-items-center p-5">
      <b-card header-tag="header" class="mt-5">
        <template #header>
          <h5 >Click a collection to start searching the lists.</h5>
        </template>
        <div class="overflow-auto border" style="height: 400px; width: 400px;">
          <div class="d-flex justify-content-center" v-for="queryCollection in queryCollections" v-bind:key="queryCollection.id">
            <router-link
                :to="{name: 'QueryCollection', params: { queryCollectionId: queryCollection.id }}"
                v-slot="{ href, route, navigate }">
              <a :href="href" @click="navigate">{{ queryCollection.name }}</a>
            </router-link>
          </div>
        </div>
      </b-card>
    </div>

  </div>
</template>

<script>
import StandardNavbar from "@/components/navbar/StandardNavbar";
import axios from "axios";

const url = process.env.VUE_APP_ARCHEDETECOR_API

export default {
  name: 'Home',
  components: {
    StandardNavbar
  },
  data() {
    return {
      queryCollections: []
    }
  },
  mounted() {
    axios.get(url + "query-collection/").then((response) => {
      this.queryCollections = response.data;
    })
  }
}
</script>

<style>
</style>